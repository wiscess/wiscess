package com.wiscess.exporter.util;

import com.wiscess.exporter.word.AssignedElement;
import com.wiscess.exporter.word.AssignedParagraph;
import com.wiscess.exporter.word.AssignedPicture;
import com.wiscess.exporter.word.AssignedRun;
import com.wiscess.exporter.word.AssignedTable;
import com.wiscess.exporter.word.AssignedTableCell;
import com.wiscess.exporter.word.IWordElement;
import com.wiscess.exporter.word.TextStyle;
import com.wiscess.exporter.word.exception.WordException;
import com.wiscess.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * word文件导出工具类
 *
 * @author wh
 * @date 2015-07-27
 */
@Slf4j
public class WordExportUtil {

    /**
     * 导出List中的内容到空白文档中
     *
     * @param os
     * @param list
     */
    public static void export(OutputStream os, List<AssignedElement> list) throws WordException {
        try {
            //创建文件
            XWPFDocument doc = createFile();
            //将list中的内容，写入到doc中
            for (AssignedElement ae : list) {
                //将ae添加到doc中
                fillElementToDoc(doc, ae);
            }

            //创建输出流，将文件内容写入到流中
            doc.write(os);
        } catch (Exception e) {
            throw new WordException("导出Word文件出错.", e);
        }
    }

    /**
     * 导出map中的内容到模板文件中
     *
     * @param os
     * @param templateFile
     * @param map
     */
    public static String export(OutputStream os, String templateFile, Map<String, IWordElement> map) {
        XWPFDocument doc = null;
        InputStream fis = null;
        try {
            //打开模板文件
            fis = WordExportUtil.class.getResourceAsStream(templateFile);

            //根据模板创建文件
            doc = createFile(fis);

            //遍历map，在doc中查找关键字，并在该位置写入list中的内容
            for (String key : map.keySet()) {
                //取出数据对象
                IWordElement ae = map.get(key);
                //根据数据对象，判断key所对应的是段落还是文本内容
                if (ae instanceof AssignedPicture) {
                    //如果是图片，则查找key所在的XWPFRun
                    AssignedPicture ap = (AssignedPicture) ae;
                    //设置照片属性
                    XWPFRun run = findRun(doc, key);
                    //替换掉run中key的内容
                    replaceRun(run, key, "");
                    addPicture(run, ap.getImgFile(), ap.getWidth(), ap.getHeight());
                } else if (ae instanceof AssignedRun) {
                    //如果是图片，则查找key所在的XWPFRun
                    XWPFRun run = findRun(doc, key);
                    AssignedRun ar = (AssignedRun) ae;
                    //未找到的时候不处理
                    if (run != null) {
                        TextStyle textStyle=ar.getTextStyle();
                        if(textStyle!=null){
                            replaceRunWithTextStyle(run,key,textStyle);
                        }else {
                            replaceRun(run, key, ar.getText());
                        }
                    }
                } else if (ae instanceof AssignedParagraph) {
                    //其他数据，查找key所在的段落
                    XWPFParagraph p = findParagraph(doc, key);
                    if (p == null) {
                        //没找到段落，创建一个新的段落
                        p = doc.createParagraph();
                    } else {
                        //找到段落中存在的Run，替换成空
                        XWPFRun run = findRun(p, key);
                        if (run != null) {
                            String newValue = run.getText(run.getTextPosition()).replaceAll(key, "");
                            run.setText(newValue, 0);
                        }
                    }
                    //添加数据到段落中
                    fillParagraph(p, (AssignedParagraph) ae);
                } else if (ae instanceof AssignedTable) {
                    //表格数据，查找key所在的表格
                    XWPFTable table = findTable(doc, key);
                    if (table == null) {
                        //没找到key所在的table，创建一个新表格
                        AssignedTable at = (AssignedTable) ae;
                        fillTable(doc.createTable(at.getRow(), at.getCol()), at);
                    } else {
                        //从key所在行插入数据
                        fillTableFromKeyRow(table, key, (AssignedTable) ae);
                    }
                } else {
                    //其他数据，查找key所在的段落
                    XWPFParagraph p = findParagraph(doc, key);
                    if (p == null) {
                        p = doc.createParagraph();
                    }
                    //添加数据到段落中
                    fillElementToParagraph(p, ae);
                }
            }

            //创建输出流，将文件内容写入到流中
            doc.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            throw new WordException("导出Word文件出错.", e);
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 生成文件
     *
     * @return
     * @throws Exception
     */
    protected static XWPFDocument createFile() throws Exception {
        return createFile(null);
    }

    protected static XWPFDocument createFile(InputStream fis) throws Exception {
        XWPFDocument doc = null;
        if (fis == null) {
            doc = new XWPFDocument();
        } else {
            try {
                doc = new XWPFDocument(fis);
            } finally {
                fis.close();
                fis = null;
            }
        }
        return doc;
    }

    /**
     * 设置
     * @param doc
     */
    protected static void setDocument(XWPFDocument doc){
    }
    /**
     * 将Element添加到doc中，根据Element不同，调用不同的方法
     *
     * @param doc
     * @param ae
     */
    protected static void fillElementToDoc(XWPFDocument doc, AssignedElement ae) {
        //根据ae类型，进行设置，允许添加到doc中的只有table和paragraph，
        //其他类型的，添加一个默认的段落，添加到该段落中
        if (ae instanceof AssignedTable) {
            //添加表格数据
            AssignedTable at = (AssignedTable) ae;
            fillTable(doc.createTable(at.getRow(), at.getCol()), at);
        } else if (ae instanceof AssignedParagraph) {
            //填充段落内容
            fillParagraph(doc.createParagraph(), (AssignedParagraph) ae);
        } else {
            //其他类型，创建一个默认的段落
            fillElementToParagraph(doc.createParagraph(), ae);
        }
    }

    /**
     * 添加Element到段落
     *
     * @param p
     * @param ae
     */
    protected static void fillElementToParagraph(XWPFParagraph p, IWordElement ae) {
        //在段落中增加一个新的Run，并获得当前的位置
        XWPFRun r = p.createRun();
        if (ae instanceof AssignedTable) {
            //在段落中增加一个新表格,1*1
            XWPFTable table = p.getDocument().insertNewTbl(p.getCTP().newCursor());
            if (table == null) {
                return;
            }
            //创建出表格的行，因为表格创建后，已经存在一行，所以row从1开始；
            AssignedTable at = (AssignedTable) ae;
            for (int row = 1; row < at.getRow(); row++) {
                table.createRow();
            }
            //创建列，因为每一行默认有一列，所以col从1开始；
            for (XWPFTableRow row : table.getRows()) {
                for (int col = 1; col < at.getCol(); col++) {
                    row.addNewTableCell();
//					table.addNewCol(); //使用这种方式无法通过row.getTableCells()获取
                }
            }
            //设置每一行，每一单元格的属性
            fillTable(table, at);
        } else if (ae instanceof AssignedParagraph) {
            //在段落中增加一个段落
            fillParagraph(p, (AssignedParagraph) ae);
        } else if (ae instanceof AssignedPicture) {
            //添加照片
            AssignedPicture ap = (AssignedPicture) ae;
            //设置照片属性
            addPicture(r, ap.getImgFile(), ap.getWidth(), ap.getHeight());
        } else if (ae instanceof AssignedRun) {
            //添加内容
            AssignedRun ar = (AssignedRun) ae;
            //设置属性
            if (ar.getFontFamily() != null && !ar.getFontFamily().equals("")) {
                r.setFontFamily(ar.getFontFamily());
            }
            if (ar.isBold()) {
                r.setBold(ar.isBold());
            }
            if (ar.getFontSize() > 0) {
                r.setFontSize(ar.getFontSize());
            }
            if (ar.getColor() != null && !ar.getColor().equals("")) {
                r.setColor(ar.getColor());
            }
            //设置内容
            r.setText(ar.getText());
        }
        //是否有回车符
        if (ae.hasCR()) {
            r.addCarriageReturn();
        }
        r.getCTR().newCursor();
    }

    /**
     * 设置table的属性，并根据AssignedTable的数据填充表格
     *
     * @param table
     * @param at
     */
    private static void fillTable(XWPFTable table, AssignedTable at) {
        //设置表格的属性
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        //指定表格样式
        CTString styleStr = tblPr.addNewTblStyle();
        styleStr.setVal("StyledTable");

        //设置表格的总宽度
        if (at.getWidth() > 0) {
            //默认TblW的type属性为STTblWidth.AUTO,即自动伸缩。所以要调整为指定类型：STTblWidth.DXA
            tblPr.getTblW().setType(STTblWidth.DXA);
            tblPr.getTblW().setW(BigInteger.valueOf(at.getWidth()));
        }

        List<XWPFTableRow> rows = table.getRows();
        int rowCt = 0;
        int colCt = 0;
        for (XWPFTableRow row : rows) {
            // 行属性 (trPr)
            CTTrPr trPr = row.getCtRow().addNewTrPr();
            // 设置默认行高 units = twentieth of a point, 360 = 0.25"
            CTHeight ht = trPr.addNewTrHeight();
            ht.setVal(BigInteger.valueOf(360));

            // get the cells in this row
            List<XWPFTableCell> cells = row.getTableCells();
            // add content to each cell
            for (XWPFTableCell cell : cells) {
                // 单元格属性 (tcPr)
                CTTcPr tcpr = cell.getCTTc().addNewTcPr();
                // 设置默认垂直居中方式 "center"
                CTVerticalJc va = tcpr.addNewVAlign();
                va.setVal(STVerticalJc.CENTER);
                // 单元格背景色
                CTShd ctshd = tcpr.addNewShd();
                ctshd.setColor("auto");
                ctshd.setVal(STShd.CLEAR);

                // 获取单元格的段落
                XWPFParagraph para = cell.getParagraphs().get(0);
                //默认段落属性
                para.setAlignment(ParagraphAlignment.LEFT);

                //添加每个单元格的内容
                AssignedTableCell atc = at.getContents(rowCt, colCt);
                if (atc != null) {
                    //重新设置该行的高度
                    if (atc.getHeight() > 0) {
                        ht.setVal(BigInteger.valueOf(atc.getHeight()));
                    }
                    //设置单元格属性
                    if (atc.getBgColor() != null && !atc.getBgColor().equals("")) {
                        ctshd.setFill(atc.getBgColor());
                    }

                    //设置段落属性
                    if (atc.getAlignment() != null) {
                        para.setAlignment(atc.getAlignment());
                    }
                    //设置单元格宽度
                    if (atc.getWidth() > 0) {
                        CTTblWidth cellw = tcpr.addNewTcW();
                        cellw.setType(STTblWidth.DXA);
                        cellw.setW(BigInteger.valueOf(atc.getWidth()));
                    }
                    //添加单元格中的内容
                    for (AssignedElement ae : atc.getCellList()) {
                        fillElementToParagraph(para, ae);
                    }
                }
                colCt++;
            } // for cell
            colCt = 0;
            rowCt++;
        } // for row
    }

    /**
     * 设置table的属性，并根据AssignedTable的数据填充表格
     *
     * @param table
     * @param at
     */
    private static void fillTableFromKeyRow(XWPFTable table, String key, AssignedTable at) {
        //先找到key所在行序号
        Integer startRow = 0;
        boolean find = false;
        for (int r = 0; r < table.getRows().size(); r++) {
            XWPFTableRow row = table.getRow(r);
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph para1 : cell.getParagraphs()) {
                    String content = para1.getParagraphText();
                    if (content.indexOf(key) >= 0) {
                        //找到
                        startRow = r;
                        find = true;
                        break;
                    }
                }
                //跳出列循环
                if (find) break;
            }
            if (find) break;
        }
        //从startRow行开始填充表格数据,记录该行数据为模板行，当表格总行数不够时，插入新行
        XWPFTableRow rowTemplate = table.getRow(startRow);
        //从模板行的下一行开始写输入
        for (int r = 0; r < at.getRow(); r++) {
            //读取当前行列对象
            XWPFTableRow row = table.getRow(startRow + r);
            if (row == null) {
                table.createRow();
                row = table.getRow(startRow + r);
                copyTableRow(row, rowTemplate);
            }
            // 行属性 (trPr)
            CTTrPr trPr = row.getCtRow().getTrPr();

            for (int c = 0; c < at.getCol(); c++) {
                //处理每个单元格的内容
                AssignedTableCell atc = at.getContents(r, c);
                if (atc != null) {
                    XWPFTableCell cell = row.getCell(c);
                    //添加数据
                    if (cell != null) {
                        // 单元格属性 (tcPr)
                        CTTcPr tcpr = cell.getCTTc().addNewTcPr();
                        // 设置默认垂直居中方式 "center"
                        CTVerticalJc va = tcpr.addNewVAlign();
                        va.setVal(STVerticalJc.CENTER);
                        // 单元格背景色
                        CTShd ctshd = tcpr.addNewShd();
                        ctshd.setColor("auto");
                        ctshd.setVal(STShd.CLEAR);

                        // 获取单元格的段落
                        XWPFParagraph para = cell.getParagraphs().get(0);
                        while (!para.getRuns().isEmpty()) {
                            para.removeRun(0);
                        }
                        //默认段落属性

                        //重新设置该行的高度
                        if (atc.getHeight() > 0) {
                            CTHeight ht = trPr.getTrHeightArray(0);
                            ht.setVal(BigInteger.valueOf(atc.getHeight()));
                        }
                        //设置单元格属性
                        if (atc.getBgColor() != null && !atc.getBgColor().equals("")) {
                            ctshd.setFill(atc.getBgColor());
                        }

                        // 设置段落属性
                        if (atc.getAlignment() != null) {
                            para.setAlignment(atc.getAlignment());
                        }
                        // 设置单元格宽度
                        if (atc.getWidth() > 0) {
                            CTTblWidth cellw = tcpr.addNewTcW();
                            cellw.setType(STTblWidth.DXA);
                            cellw.setW(BigInteger.valueOf(atc.getWidth()));
                        }
                        // 添加单元格中的内容
                        for (AssignedElement ae : atc.getCellList()) {
                            fillElementToParagraph(para, ae);
                        }
                    }
                }

            }
        }
    }

    /**
     * 复制行，从source到target
     *
     * @param target
     * @param source
     */
    public static void copyTableRow(XWPFTableRow target, XWPFTableRow source) {
        // 复制样式
        if (source.getCtRow() != null) {
            target.getCtRow().setTrPr(source.getCtRow().getTrPr());
        }
        // 复制单元格
        for (int i = 0; i < source.getTableCells().size(); i++) {
            XWPFTableCell cell1 = target.getCell(i);
            XWPFTableCell cell2 = source.getCell(i);
            if (cell1 == null) {
                cell1 = target.addNewTableCell();
            }
            copyTableCell(cell1, cell2);
        }
    }

    /**
     * 复制单元格，从source到target
     *
     * @param target
     * @param source
     */
    public static void copyTableCell(XWPFTableCell target, XWPFTableCell source) {
        // 列属性
        if (source.getCTTc() != null) {
            target.getCTTc().setTcPr(source.getCTTc().getTcPr());
        }
        // 删除段落
        for (int pos = target.getParagraphs().size() - 1; pos >= 0; pos--) {
            target.removeParagraph(pos);
        }
        // 添加段落
        for (XWPFParagraph sp : source.getParagraphs()) {
            XWPFParagraph targetP = target.addParagraph();
            copyParagraph(targetP, sp);
        }
    }

    /**
     * 复制段落，从source到target
     *
     * @param target
     * @param source
     */
    public static void copyParagraph(XWPFParagraph target, XWPFParagraph source) {

        // 设置段落样式
        target.getCTP().setPPr(source.getCTP().getPPr());

        // 移除所有的run
        for (int pos = target.getRuns().size() - 1; pos >= 0; pos--) {
            target.removeRun(pos);
        }

        // copy 新的run
        for (XWPFRun s : source.getRuns()) {
            XWPFRun targetrun = target.createRun();
            copyRun(targetrun, s);
        }

    }

    /**
     * 复制RUN，从source到target
     *
     * @param target
     * @param source
     */
    public static void copyRun(XWPFRun target, XWPFRun source) {
        // 设置run属性
        target.getCTR().setRPr(source.getCTR().getRPr());
        // 设置文本
        target.setText(source.text());
        // 处理图片
        List<XWPFPicture> pictures = source.getEmbeddedPictures();

        for (XWPFPicture picture : pictures) {
            try {
                copyPicture(target, picture);
            } catch (InvalidFormatException e) {
                log.error("copyRun", e);
            } catch (IOException e) {
                log.error("copyRun", e);
            }
        }
    }

    /**
     * 复制图片到target
     *
     * @param target
     * @param picture
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static void copyPicture(XWPFRun target, XWPFPicture picture) throws IOException, InvalidFormatException {

        String filename = picture.getPictureData().getFileName();
        InputStream pictureData = new ByteArrayInputStream(picture
                .getPictureData().getData());
        int pictureType = picture.getPictureData().getPictureType();
        int width = (int) picture.getCTPicture().getSpPr().getXfrm().getExt()
                .getCx();

        int height = (int) picture.getCTPicture().getSpPr().getXfrm().getExt()
                .getCy();

        // target.addBreak();
        target.addPicture(pictureData, pictureType, filename, width, height);
        // target.addBreak(BreakType.PAGE);
    }

    /**
     * 填充段落
     *
     * @param p
     * @param ap
     */
    private static void fillParagraph(XWPFParagraph p, AssignedParagraph ap) {
        //设置段落属性
        if (ap.getAlignment() != null) {
            p.setAlignment(ap.getAlignment());
        }
        if (ap.isPageBreak()) {
            p.setPageBreak(ap.isPageBreak());
        }
        //添加段落中的内容
        for (IWordElement ae : ap.getContentList()) {
            fillElementToParagraph(p, ae);
        }
    }

    /**
     * 添加图片
     *
     * @param r
     * @param imgFile1
     * @param width
     * @param height
     */
    private static void addPicture(XWPFRun r, String imgFile1, int width, int height) {
        int format;
        String imgFile = imgFile1.toLowerCase();
        if (imgFile.endsWith(".emf")) format = XWPFDocument.PICTURE_TYPE_EMF;
        else if (imgFile.endsWith(".wmf")) format = XWPFDocument.PICTURE_TYPE_WMF;
        else if (imgFile.endsWith(".pict")) format = XWPFDocument.PICTURE_TYPE_PICT;
        else if (imgFile.endsWith(".jpeg") || imgFile.endsWith(".jpg")) format = XWPFDocument.PICTURE_TYPE_JPEG;
        else if (imgFile.endsWith(".png")) format = XWPFDocument.PICTURE_TYPE_PNG;
        else if (imgFile.endsWith(".dib")) format = XWPFDocument.PICTURE_TYPE_DIB;
        else if (imgFile.endsWith(".gif")) format = XWPFDocument.PICTURE_TYPE_GIF;
        else if (imgFile.endsWith(".tiff")) format = XWPFDocument.PICTURE_TYPE_TIFF;
        else if (imgFile.endsWith(".eps")) format = XWPFDocument.PICTURE_TYPE_EPS;
        else if (imgFile.endsWith(".bmp")) format = XWPFDocument.PICTURE_TYPE_BMP;
        else if (imgFile.endsWith(".wpg")) format = XWPFDocument.PICTURE_TYPE_WPG;
        else {
            format = XWPFDocument.PICTURE_TYPE_JPEG;
        }
        try {
            if (imgFile.startsWith("http")) {
                URL url = new URL(imgFile1);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5 * 1000);
                conn.connect();
                InputStream in = conn.getInputStream();
                r.addPicture(in, format, imgFile, Units.toEMU(width), Units.toEMU(height));

                in.close();
            } else {
                r.addPicture(new FileInputStream(imgFile), format, imgFile, Units.toEMU(width), Units.toEMU(height));
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在doc中搜索key
     *
     * @param doc
     * @param key
     * @return
     */
    private static XWPFRun findRun(XWPFDocument doc, String key) {
        //遍历文档中的所有段落
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        XWPFParagraph para;
        while (iterator.hasNext()) {
            para = iterator.next();
            //在段落中查找
            XWPFRun r = findRun(para, key);
            if (r != null)
                return r;
        }
        //遍历文档中所有表格
        Iterator<XWPFTable> iteratorTable = doc.getTablesIterator();
        XWPFTable table;
        while (iteratorTable.hasNext()) {
            table = iteratorTable.next();
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph para1 : cell.getParagraphs()) {
                        //在段落中查找
                        XWPFRun r = findRun(para1, key);
                        if (r != null)
                            return r;
                    }
                }
            }
        }
        //未找到run，在最后创建一个段落，并创建一个run返回
        log.info("文档中未找到指定的关键字：" + key);
        XWPFRun run = doc.createParagraph().createRun();
        run.setText("", 0);
        return run;
    }

    /**
     * 在Paragraph中搜索
     *
     * @param para
     * @param key
     */
    private static XWPFRun findRun(XWPFParagraph para, String key) {
        List<XWPFRun> runList = para.getRuns();
        //记录当前段落的所有内容
        StringBuffer sb = new StringBuffer();
        for (XWPFRun r : runList) {
            int pos = r.getTextPosition();
            String content = r.getText(pos);
            sb.append(content);
            if (content != null && content.indexOf(key) >= 0) {
                //如果当前内容中包含关键字，则返回当前Run
                return r;
            }
        }
        //未发现完整的key以后，判断sb中是否包含了key
        if (sb.toString().indexOf(key) >= 0) {
            //包含了key，说明模板中的key被拆分为多个run，需对当前段落的run重新处理
            //TODO fix
        }
        return null;
    }

    /**
     * 替换掉run中key的内容为新值
     *
     * @param run
     * @param key
     * @param value
     */
    private static void replaceRun(XWPFRun run, String key, String value) {
        //避免run中含有除key以外的数据，所以不能直接替换
        String newValue = run.getText(run.getTextPosition()).replaceAll(key, value == null ? "" : value);
        run.setText(newValue, 0);
    }

    /**
     * 替换run中key的内容，并设置为指定的样式
     * @param run
     * @param key
     * @param textStyle
     */
    private static void replaceRunWithTextStyle(XWPFRun run, String key,TextStyle textStyle){
        String newValue = run.getText(run.getTextPosition()).replaceAll(key, textStyle.getText() == null ? "" : textStyle.getText());
        run.setText(newValue, 0);




    }

    /**
     * 在文档中查找key所在的段落
     *
     * @param doc
     * @param key
     * @return
     */
    private static XWPFParagraph findParagraph(XWPFDocument doc, String key) {
        //遍历文档中的所有段落
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        XWPFParagraph para;
        while (iterator.hasNext()) {
            para = iterator.next();
            String content = para.getParagraphText();
            if (content.indexOf(key) >= 0) {
                return para;
            }
        }
        //遍历文档中所有表格
        Iterator<XWPFTable> iteratorTable = doc.getTablesIterator();
        XWPFTable table;
        while (iteratorTable.hasNext()) {
            table = iteratorTable.next();
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph para1 : cell.getParagraphs()) {
                        String content = para1.getParagraphText();
                        if (content.indexOf(key) >= 0) {
                            return para1;
                        }
                    }
                }
            }
        }

        return null;
    }

    private static XWPFTable findTable(XWPFDocument doc, String key) {
        //遍历文档中所有表格
        Iterator<XWPFTable> iteratorTable = doc.getTablesIterator();
        XWPFTable table;
        while (iteratorTable.hasNext()) {
            table = iteratorTable.next();
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph para1 : cell.getParagraphs()) {
                        String content = para1.getParagraphText();
                        if (content.indexOf(key) >= 0) {
                            return table;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * 解析html文件内容成文字、段落、image等数据
     *
     * @param prefixUrl
     * @param workPath
     * @param content
     * @return
     */
    public static List<AssignedElement> parserHtmlContent(String prefixUrl, String workPath, String content) {
        List<AssignedElement> contentList = new ArrayList<AssignedElement>();
        content = html(content);

        Pattern p = Pattern.compile("<([^>]*)>");
        Pattern imgPattern = Pattern.compile("src=[\'|\"]*([^\'|\"]*)[\'|\"]*");
        Pattern imgWPattern = Pattern.compile("width=[\'|\"]*([^\'|\"]*)[\'|\"]*");
        Pattern imgHPattern = Pattern.compile("height=[\'|\"]*([^\'|\"]*)[\'|\"]*");
        Matcher m = p.matcher(content);
        AssignedParagraph para = new AssignedParagraph();
        boolean flag = false;
        while (m.find()) {
            if (m.start() > 0) {
                //图片前有文字
                para.addElement(new AssignedRun(content.substring(0, m.start())));
                content = content.substring(m.start());
                flag = true;
            }
            String tag = m.group(1).toLowerCase();
            if (tag.startsWith("p")) {
                if (flag)
                    contentList.add(para);
                para = new AssignedParagraph();
                flag = false;
                content = content.replaceFirst(m.group(), "");
            } else if (tag.startsWith("/p")) {
                if (flag)
                    contentList.add(para);
                para = new AssignedParagraph();
                flag = false;
                content = content.replaceFirst(m.group(), "");
            } else if (tag.startsWith("br")) {
                para.addElement(new AssignedRun("").addCR());
                flag = true;
                content = content.replaceFirst(m.group(), "");
            } else if (tag.startsWith("/br")) {
                para.addElement(new AssignedRun("").addCR());
                flag = true;
                content = content.replaceFirst(m.group(), "");
            } else if (tag.startsWith("img")) {
                Matcher imageMatcher = imgPattern.matcher(m.group(1));
                Matcher imageWMatcher = imgWPattern.matcher(m.group(1));
                Matcher imageHMatcher = imgHPattern.matcher(m.group(1));
                if (imageMatcher.find()) {
                    String src = imageMatcher.group(1);
                    src = src.replace(prefixUrl, "");
                    String imgfile = workPath + src;
                    File imgF = new File(imgfile);
                    if (imgF.exists()) {
                        //计算图像的宽高
                        int width = 400;
                        int height = 300;
                        if (imageWMatcher.find()) {
                            width = Integer.parseInt(imageWMatcher.group(1));
                        }
                        if (imageHMatcher.find()) {
                            height = Integer.parseInt(imageHMatcher.group(1));
                        }
                        para.addElement(new AssignedPicture(imgfile, width, height));
                        flag = true;
                    }
                }

                content = content.replace(m.group(), "");
            } else {
                //其他<>标签去掉
                try {
                    content = content.replace(m.group(), "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            m = p.matcher(content);
        }
        if (StringUtils.isNotEmpty(content)) {
            para.addElement(new AssignedRun(content));
            flag = true;
        }
        if (flag)
            contentList.add(para);
        return contentList;
    }

    /**
     * 格式化HTML文本
     *
     * @param content
     * @return
     */
    public static String html(String content) {
        if (content == null) return "";
        String html = content;
        html = StringUtils.replace(html, "&amp;", "&");
        html = StringUtils.replace(html, "&apos;", "'");
        html = StringUtils.replace(html, "&quot;", "\"");
        html = StringUtils.replace(html, "&ldquo;", "“");
        html = StringUtils.replace(html, "&rdquo;", "”");

        //html = StringUtils.replace(html, "&nbsp;&nbsp;","\t");// 替换跳格
        html = StringUtils.replace(html, "&nbsp;", " ");// 替换空格
        html = StringUtils.replace(html, "&lt;", "<");
        html = StringUtils.replace(html, "&gt;", ">");
        html = StringUtils.replace(html, "&times;", "×");
        html = StringUtils.replace(html, "&divide;", "÷");
        html = StringUtils.replace(html, "&ensp;", "         ");
        html = StringUtils.replace(html, "&emsp;", "         ");
        //html = StringUtils.replace(html, "\\?"," ");
        return html;
    }
}

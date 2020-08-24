package com.wiscess.exporter.word;


import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

import java.util.ArrayList;
import java.util.List;

/**
 * 段落信息
 *
 * @author wh
 */
public class AssignedParagraph extends AssignedElement {

    //段落基本属性
    private ParagraphAlignment alignment;

    private boolean pageBreak = false;

    //段落中的内容
    private List<IWordElement> contentList;

    public AssignedParagraph() {
        contentList = new ArrayList<IWordElement>();
    }

    public AssignedParagraph addElement(IWordElement ae) {
        if (ae instanceof AssignedPicture || ae instanceof AssignedRun || ae instanceof AssignedTable) {
            this.contentList.add(ae);
        } else {
            //throw new ManagerException("不支持的内容");
            this.contentList.add(ae);
        }
        return this;
    }

    public List<IWordElement> getContentList() {
        return contentList;
    }

    public ParagraphAlignment getAlignment() {
        return alignment;
    }

    public AssignedParagraph setAlignment(ParagraphAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public boolean isPageBreak() {
        return pageBreak;
    }

    public void setPageBreak(boolean pageBreak) {
        this.pageBreak = pageBreak;
    }

}

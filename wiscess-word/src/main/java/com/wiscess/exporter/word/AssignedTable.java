package com.wiscess.exporter.word;


/**
 * 指定的表格
 *
 * @author wh
 */
public class AssignedTable extends AssignedElement {

    private int row; //表格的行数
    private int col; //表格的列数
    private int width;//表格的总宽度
    private AssignedTableCell[][] contents;

    public AssignedTable(int row, int col) {
        this.row = row;
        this.col = col;
        contents = new AssignedTableCell[row][col];
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public AssignedTableCell getContents(int row, int col) {
        return contents[row][col];
    }

    public void setContents(int row, int col, AssignedTableCell atc) {
        this.contents[row][col] = atc;
    }

    public void setContents(int row, int col, String value) {

        this.contents[row][col] = new AssignedTableCell(value);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "content=" + contents;
    }
}

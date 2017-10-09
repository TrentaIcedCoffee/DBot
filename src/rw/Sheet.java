package rw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import exception.CellIndexOutOfBoundException;
import exception.RowIndexOutOfBoundException;

/**
 * 	Sheet implements Iterable<Row>
 *	public Sheet(File file) throws InvalidFormatException, FileNotFoundException, IOException
 *	public void overrideOriginal() throws FileNotFoundException, IOException
 *	public Cell get(int i, int j)
 *	public void write(int i, int j, String val)
 *	public Row getRowAt(int index)
 *	public int getRowCount()
 *	public int getColCount()
 *	public Row getFirstRow() throws RowIndexOutOfBoundException
 */
public class Sheet implements Iterable<Row> {
	private File file;
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private int rowCount;
	private int colCount;
	
	public Sheet(File file) throws InvalidFormatException, FileNotFoundException, IOException {
		this.file = file;
		this.workbook = new XSSFWorkbook(new FileInputStream(file));
		this.sheet = this.workbook.getSheetAt(0);
		this.rowCount= this.sheet.getLastRowNum() + 1;
		this.colCount = makeColCount();
		ini();
	}
	
	/**
	 * override original workbook file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void overrideOriginal() throws FileNotFoundException, IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		workbook.write(fileOutputStream);
		fileOutputStream.close();
	}
	
	/**
	 * used in Reader
	 * @param i
	 * @param j
	 * @return Cell
	 */
	public Cell get(int i, int j) {
		if (!isValidIndex(i, j)) {
			throw new CellIndexOutOfBoundException(String.format("Cell index out of bound, index: (%d, %d) bound: (%d, %d) inclusive", 
					i, j, rowCount - 1, colCount - 1));
		}
		return sheet.getRow(i).getCell(j);
	}
	
	/**
	 * if no cell, create it, used in Writer
	 * @param i
	 * @param j
	 * @param val
	 */
	public void write(int i, int j, String val) {
		if (sheet.getRow(i) == null) {
			sheet.createRow(i);
		}
		if (sheet.getRow(i).getCell(j) == null) {
			sheet.getRow(i).createCell(j);
		}
		sheet.getRow(i).getCell(j).setCellValue(val);
	}
	
	public Row getRowAt(int index) {
		if (!isValidRowIndex(index)) {
			throw new CellIndexOutOfBoundException("Row Index out of bound, index: " + index + " but bound: [0, " + rowCount + ")");
		}
		return sheet.getRow(index);
	}
	
	public int getRowCount() {
		return rowCount;
	}
	
	public int getColCount() {
		return colCount;
	}
	
	public Row getFirstRow() throws RowIndexOutOfBoundException {
		if (!isValidRowIndex(0)) {
			throw new RowIndexOutOfBoundException("Row Index out of bound, index: " + 0 + " but bound: [0, " + rowCount + ")");
		}
		return sheet.getRow(0);
	}
	
	@Override @Deprecated
	public Iterator<Row> iterator() {
		return new RowIterator(this);
	}
	
	private class RowIterator implements Iterator<Row> {
		private Sheet sheet;
		private int indexMax;
		private int index;
		
		public RowIterator(Sheet sheet) {
			this.sheet = sheet;
			this.indexMax = this.sheet.rowCount;
			this.index = 0;
		}
		
		@Override
		public boolean hasNext() {
			return index < indexMax;
		}

		@Override
		public Row next() {
			if (!hasNext()) {
				throw new NoSuchElementException("no next element");
			}
			return sheet.getRowAt(index++);
		}
		
		public void remove() {
			throw new UnsupportedOperationException("remove() in RowIterator is not supported");
		}
	}
	
	/**
	 * set every empty cell in-bound ""
	 */
	private void ini() {
		for (int i = 0; i < rowCount; i++) {
			if (sheet.getRow(i) == null) {
				sheet.createRow(i);
			}
			for (int j = 0; j < colCount; j++) {
				if (sheet.getRow(i).getCell(j) == null) {
					sheet.getRow(i).createCell(j).setCellValue("");;
				}
			}
		}
	}
	
	/**
	 * @return max(colCount of each row)
	 */
	private int makeColCount() {
		int maxColCount = 0;
		for (Row row : sheet) {
			maxColCount = Math.max(maxColCount, row.getLastCellNum());
		}
		return maxColCount;
	}
	
	/**
	 * Only used at Reader.class
	 * @param i
	 * @param j
	 * @return true if i, j under rowCount, colCount
	 */
	private boolean isValidIndex(int i, int j) {
		return isValidRowIndex(i) && isValidColIndex(j);
	}
	
	private boolean isValidRowIndex(int i) {
		return 0 <= i && i <= rowCount - 1;
	}
	
	private boolean isValidColIndex(int j) {
		return 0 <= j && j <= colCount - 1;
	}
}

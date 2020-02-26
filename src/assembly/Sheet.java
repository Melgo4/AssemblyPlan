package assembly;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.Iterator;

public class Sheet {

	protected XSSFSheet sheet;
	protected Cell[][] cells;

	protected int rows;
	protected int columns;

	protected BoundingBox boundingBox = new BoundingBox(0, 0, 0, 0);

	public Sheet(XSSFSheet sheet) {
		this.sheet = sheet;
		this.initializeDimensions();
		this.createCells();
	}

	private void createCells() {
		this.cells = new Cell[this.rows][this.columns];

		int minY = 0;
		int maxY = 0;

		for(int row = 0; row < this.rows; row++) {
			XSSFRow internalRow = this.sheet.getRow(row);
			maxY += internalRow.getHeight();

			int minX = 0;
			int maxX = 0;

			for(int column = 0; column < this.columns; column++) {
				XSSFCell internalCell = internalRow.getCell(column);
				maxX += this.sheet.getColumnWidth(column) / 2.5;

				this.cells[row][column] = new Cell(new BoundingBox(minX, maxX, minY, maxY), internalCell);
				this.boundingBox.encompass(this.cells[row][column].getBoundingBox());
				minX = maxX;
			}

			minY = maxY;
		}

		for(CellRangeAddress region: this.sheet.getMergedRegions()) {
			Iterator<CellAddress> it = region.iterator();
			Cell parent = null;

			while(it.hasNext()) {
				CellAddress cellAddress = it.next();
				Cell cell = this.cells[cellAddress.getRow()][cellAddress.getColumn()];

				if(parent == null) {
					parent = cell;
					continue;
				}

				cell.setParentCell(parent);
				parent.addChild(cell);
			}
		}
	}

	private void initializeDimensions() {
		this.rows = this.sheet.getLastRowNum();

		if(this.rows > 0 || this.sheet.getPhysicalNumberOfRows() > 0) {
			this.rows++;
		}

		for(int i = 0; i < this.rows; i++) {
			int col = this.sheet.getRow(i).getLastCellNum();

			if(col > 0 || this.sheet.getRow(i).getPhysicalNumberOfCells() > 0) {
				col++;
			}

			if(col > this.columns) {
				this.columns = col;
			}
		}
	}

	public int getRows() {
		return this.rows;
	}

	public int getColumns() {
		return this.columns;
	}

	public XSSFSheet getInternalSheet() {
		return this.sheet;
	}

	public void render(Pane pane, Stage stage) {
		pane.getChildren().clear();
		stage.getScene().setFill(Color.LIGHTGRAY);

		double min = Math.min(stage.getScene().getHeight(), stage.getScene().getWidth());
		double sizeMin = Math.min(this.boundingBox.maxX - this.boundingBox.minX, this.boundingBox.maxY - this.boundingBox.minY);

		double scale = min / sizeMin;
		double offsetX = stage.getScene().getWidth() / 2.0F - ((this.boundingBox.maxX - this.boundingBox.minX) * scale) / 2.0D;
		double offsetY = stage.getScene().getHeight() / 2.0F - ((this.boundingBox.maxY - this.boundingBox.minY) * scale) / 2.0D;

		Polygon polygon = new Polygon(
				this.boundingBox.minX * scale + offsetX, this.boundingBox.minY * scale + offsetY,
				this.boundingBox.maxX * scale + offsetX, this.boundingBox.minY * scale + offsetY,
				this.boundingBox.maxX * scale + offsetX, this.boundingBox.maxY * scale + offsetY,
				this.boundingBox.minX * scale + offsetX, this.boundingBox.maxY * scale + offsetY
		);

		polygon.toBack();
		polygon.setStroke(Color.BLACK);
		polygon.setFill(Color.SNOW);
		pane.getChildren().add(polygon);

		for(int row = 0; row < this.rows; row++) {
			for(int column = 0; column < this.columns; column++) {
				Cell cell = this.cells[row][column];
				if(cell.isMergedCell())continue;
				cell.render(pane, scale, offsetX, offsetY);
			}
		}
	}

}

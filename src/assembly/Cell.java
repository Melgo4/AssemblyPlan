package assembly;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.util.ArrayList;
import java.util.List;

public class Cell {

	private BoundingBox boundingBox;
	private final XSSFCell cell;

	private Cell parentCell;
	private List<Cell> children = new ArrayList<>();

	public Cell(BoundingBox boundingBox, XSSFCell cell) {
		this.boundingBox = boundingBox;
		this.cell = cell;
	}

	public BoundingBox getBoundingBox() {
		return this.boundingBox;
	}

	public XSSFCell getInternalCell() {
		return this.cell;
	}

	public Cell getParentCell() {
		return this.isMergedCell() ? this : this.parentCell;
	}

	public void setParentCell(Cell parentCell) {
		this.parentCell = parentCell;
	}

	public List<Cell> getChildren() {
		return this.children;
	}

	public boolean isMergedCell() {
		return this.parentCell != null;
	}

	public void addChild(Cell child) {
		this.children.add(child);
		this.boundingBox.encompass(child.getBoundingBox());
	}

	public void render(Pane pane, double scale, double offsetX, double offsetY) {
		if(this.cell == null)return;

		Label text = new Label(this.cell.getStringCellValue());
		text.setTranslateX(this.boundingBox.minX * scale + offsetX);
		text.setTranslateY(this.boundingBox.minY * scale + offsetY);
		XSSFFont internalFont = this.cell.getCellStyle().getFont();
		Font font = new Font(internalFont.getFontName(), internalFont.getFontHeightInPoints());
		text.setFont(font);

		pane.getChildren().add(text);

		XSSFCellStyle style = this.cell.getCellStyle();
		boolean hasBorders = style.getBorderTop() != BorderStyle.NONE || style.getBorderLeft() != BorderStyle.NONE;
		if(!hasBorders)return;

		Line line1 = new Line(this.boundingBox.minX * scale + offsetX, this.boundingBox.minY * scale + offsetY, this.boundingBox.maxX * scale + offsetX, this.boundingBox.minY * scale + offsetY);
		Line line2 = new Line(this.boundingBox.minX * scale + offsetX, this.boundingBox.minY * scale + offsetY, this.boundingBox.minX * scale + offsetX, this.boundingBox.maxY * scale + offsetY);
		Line line3 = new Line(this.boundingBox.maxX * scale + offsetX, this.boundingBox.maxY * scale + offsetY, this.boundingBox.maxX * scale + offsetX, this.boundingBox.minY * scale + offsetY);
		Line line4 = new Line(this.boundingBox.maxX * scale + offsetX, this.boundingBox.maxY * scale + offsetY, this.boundingBox.minX * scale + offsetX, this.boundingBox.maxY * scale + offsetY);

		line1.setStroke(style.getBorderTop() == BorderStyle.NONE ? Color.WHITE : Color.BLACK);
		line2.setStroke(style.getBorderLeft() == BorderStyle.NONE ? Color.WHITE : Color.BLACK);
		line3.setStroke(style.getBorderRight() == BorderStyle.NONE ? Color.WHITE : Color.BLACK);
		line4.setStroke(style.getBorderBottom() == BorderStyle.NONE ? Color.WHITE : Color.BLACK);

		Polygon polygon = new Polygon(
				this.boundingBox.minX * scale + offsetX, this.boundingBox.minY * scale + offsetY,
				this.boundingBox.maxX * scale + offsetX, this.boundingBox.minY * scale + offsetY,
				this.boundingBox.maxX * scale + offsetX, this.boundingBox.maxY * scale + offsetY,
				this.boundingBox.minX * scale + offsetX, this.boundingBox.maxY * scale + offsetY
		);

		polygon.setStroke(Color.TRANSPARENT);
		polygon.setFill(Color.TRANSPARENT);

		polygon.setOnMouseEntered(e -> {
			polygon.setFill(Color.WHEAT);
		});

		polygon.setOnMouseExited(e -> {
			polygon.setFill(Color.TRANSPARENT);
		});

		pane.getChildren().addAll(line1, line2, line3, line4, polygon);
	}

}

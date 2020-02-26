package assembly;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class Workbook {

	XSSFWorkbook sheets;

	public Workbook(String loc) throws IOException {
		this.sheets = new XSSFWorkbook(new FileInputStream(loc));
	}

	public Sheet getSheet(String name) {
		return new Sheet(this.sheets.getSheet(name));
	}

}

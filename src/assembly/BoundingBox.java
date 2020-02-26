package assembly;

public class BoundingBox {

	public int minX;
	public int maxX;
	public int minY;
	public int maxY;

	public BoundingBox(int minX, int maxX, int minY, int maxY) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	public void encompass(BoundingBox boundingBox) {
		this.minX = Math.min(this.minX, boundingBox.minX);
		this.minY = Math.min(this.minY, boundingBox.minY);
		this.maxX = Math.max(this.maxX, boundingBox.maxX);
		this.maxY = Math.max(this.maxY, boundingBox.maxY);
	}

}

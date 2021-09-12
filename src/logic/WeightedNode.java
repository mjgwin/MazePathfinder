package logic;

public class WeightedNode extends Vector{
	
	private float f, g, h;

	public WeightedNode(int x, int y, int data) {
		super(x, y, data);
		f = 0;
		g = 0;
		h = 0;
	}
	
	public WeightedNode(int x, int y, int data, float f, float g, float h) {
		super(x, y, data);
		this.f = f;
		this.g = g;
		this.h = h;
	}

	public float getF() {
		return f;
	}

	public void setF(float f) {
		this.f = f;
	}

	public float getG() {
		return g;
	}

	public void setG(float g) {
		this.g = g;
	}

	public float getH() {
		return h;
	}

	public void setH(float h) {
		this.h = h;
	}
	

}

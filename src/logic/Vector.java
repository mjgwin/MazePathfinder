package logic;

public class Vector {

	private int x, y;
	private int data;
	
	public Vector(int x, int y, int data) {
		this.x = x;
		this.y = y;
		this.data = data;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return x + "," + y;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o.equals(null)) return false;
		Vector other = (Vector)o;
		return other.getX() == this.getX() && other.getY() == this.getY() && other.getData() == this.getData();
	}
}

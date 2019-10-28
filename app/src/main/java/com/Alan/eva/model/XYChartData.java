package com.Alan.eva.model;

public class XYChartData {
	private String lineName; // 线的名称
	private int paintColor; // 线的颜色
	private String[] coordinateX;// x的坐标值
	private float[] coordinateY;// Y的坐标值

	public XYChartData() {
		super();
	}

	public XYChartData(String lineName, String[] coordinateX, float[] coordinateY, int paintColor) {
		this.lineName = lineName;
		this.coordinateX = coordinateX;
		this.coordinateY = coordinateY;
		this.paintColor = paintColor;
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public int getPaintColor() {
		return paintColor;
	}

	public void setPaintColor(int paintColor) {
		this.paintColor = paintColor;
	}

	public String[] getCoordinateX() {
		return coordinateX;
	}

	public void setCoordinateX(String[] coordinateX) {
		this.coordinateX = coordinateX;
	}

	public float[] getCoordinateY() {
		return coordinateY;
	}

	public void setCoordinateY(float[] coordinateY) {
		this.coordinateY = coordinateY;
	}

}

package com.chuangju.pathnote.lib;

public enum DrawingType {
    HandDraw,
    Line,
    LineDash,
    Circle,
    Rect,
    Ellipse,
    Curve,
    Arc,
    Text,
    Picture,
    Mask,
    Rubber,
    Pointer,
    Selector,
    Math,
    Grid,
    XAxis,
    YAxis;
    private int type;

    static {
        HandDraw.type = 0x01;//手绘
        Line.type = 0x10;//直线
        LineDash.type = 0x11;//虚线
        Circle.type = 0x12;//圆
        Rect.type = 0x13;//矩形
        Ellipse.type = 0x14;//椭圆
        Curve.type = 0x15;//曲线
        Arc.type = 0x16;//弧线
        Text.type = 0x17;//文字
        Picture.type = 0x20;//图像
        Mask.type = 0x21;//蒙板
        Rubber.type = 0x22;//橡皮
        Pointer.type = 0x23;//教鞭
        Selector.type = 0x24;//选择器
        Math.type = 0x25;//数学形状
        Grid.type = 0x30;//网格
        XAxis.type = 0x31; //X
        YAxis.type = 0x32; //Y
    }

    public static DrawingType from(int type) throws Exception {
        if (type == 9)
            return DrawingType.HandDraw;
        for (DrawingType mDrawingType : DrawingType.values()) {
            if (mDrawingType.type == type)
                return mDrawingType;
        }
        throw new Exception("unKnown DrawingType");
    }

    public int getType() {
        return type;
    }
}

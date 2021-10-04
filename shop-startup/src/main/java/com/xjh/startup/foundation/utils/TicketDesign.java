/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xjh.startup.foundation.constants.EnumComType;


/**
 * @author 36181
 */
public class TicketDesign {

    private double widthMM = 80;
    private static final int dpi = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
    //private JSONArray jSONArray;
    private List<TicketCom> ticketComs = new ArrayList<>();
    private int yMax;
    //private int initHeight = 680;
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private int ticketDesignType;
    private String ticketDesignName;
    private int ticketDesignId;
    private long ticketDesignCreatTime;

    public TicketDesign() {
        ticketDesignType = Printer.TYPE_80;
        paddingLeft = getPix(2);
        paddingTop = getPix(5);
        paddingRight = getPix(1);
        paddingBottom = getPix(5);
        yMax = paddingTop + paddingBottom;

        ticketComs = Collections.synchronizedList(new ArrayList<>());

    }

    public TicketDesign(JSONArray jSONArray) {
        loadJSONArray(jSONArray);
    }

    public long getTicketDesignCreatTime() {
        return ticketDesignCreatTime;
    }

    public void setTicketDesignCreatTime(long ticketDesignCreatTime) {
        this.ticketDesignCreatTime = ticketDesignCreatTime;
    }

    public int getTicketDesignId() {
        return ticketDesignId;
    }

    public void setTicketDesignId(int ticketDesignId) {
        this.ticketDesignId = ticketDesignId;
    }

    public String getTicketDesignName() {
        return ticketDesignName;
    }

    public void setTicketDesignName(String ticketDesignName) {
        this.ticketDesignName = ticketDesignName;
    }

    public int getTicketDesignType() {
        return ticketDesignType;
    }

    public void setTicketDesignTo58MM() {
        this.widthMM = 56;
        ticketDesignType = Printer.TYPE_58;
    }

    public void setTicketDesignTo80MM() {
        this.widthMM = 80;
        ticketDesignType = Printer.TYPE_80;
    }

    public List<TicketCom> getTicketComs() {
        return ticketComs;
    }

    public Font[] getFonts() {
        return null;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public int getDrawMinX() {
        return paddingLeft;
    }

    public int getDrawMaxX() {
        return getWidth() - paddingRight;
    }

    public TicketCom getNextTicketCom(TicketCom ticketCom) {
        int index = ticketComs.indexOf(ticketCom);
        if (index != -1 && index < ticketComs.size() - 1) {
            return ticketComs.get(index + 1);
        }
        return null;
    }

    public TicketCom getLastTicketCom(TicketCom ticketCom) {
        int index = ticketComs.indexOf(ticketCom);
        if (index > 0) {
            return ticketComs.get(index - 1);
        }
        return null;
    }

    public int indexOf(TicketCom ticketCom) {
        return ticketComs.indexOf(ticketCom);
    }


    public boolean isHaveTicketComWidthName(String name) {
        return getTicketCom(name) != null;
    }

    public boolean isHaveTicketComWidthNameExcludeOneTicketCom(String name, TicketCom ticketCom) {
        TicketCom another = getTicketCom(name);
        if (another != null) {
            //System.out.println("" + another + ",," + ticketCom);
            return another.equals(ticketCom) == false;
        }
        return false;
    }

    public TicketCom getTicketCom(String name) {
        for (int i = 0; i < ticketComs.size(); i++) {
            if (ticketComs.get(i).getName().equals(name)) {
                return ticketComs.get(i);
            }
        }
        return null;
    }

    public int getWidth() {
        return getPix(widthMM);
    }

    public int getHeight() {
        //return yMax > initHeight ? yMax : initHeight;
        return yMax;
    }

    public Dimension getPreferredSize() {
        return getSize();
    }

    public Dimension getSize() {
        return new Dimension(getWidth(), getHeight());
    }

    public static int getPix(double mm) {
        return (int) ((mm / 25.4f) * dpi);
    }

    private int startX;
    private int startY;

    public int getNextComStartX() {
        return startX;
    }

    public int getNextComStartY() {
        return startY;
    }

    public void setNextComStartX(int startX) {
        this.startX = startX;
    }

    public void setNextComStartY(int startY) {
        this.startY = startY;
    }

    public int getMinX() {
        return getPaddingLeft();
    }

    private int nowMouseX;
    private int nowMouseY;

    public int getNowMouseX() {
        return nowMouseX;
    }

    public int getNowMouseY() {
        return nowMouseY;
    }

    public int getMaxX() {
        return getWidth() - getPaddingRight();
    }

    public void paint(Graphics g) {
        //System.out.println("get==>["+getHeight()+"]" + System.currentTimeMillis() + "------------->");
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setColor(Color.white);

        graphics2D.fillRect(0, 0, getWidth(), getHeight());
        graphics2D.setColor(Color.BLACK);
        setNextComStartX(paddingLeft);
        setNextComStartY(paddingTop);

        if (ticketComs.isEmpty() == false) {
            TicketCom ticketCom = ticketComs.get(0);
            if (ticketCom instanceof TicketComText) {
                TicketComText ticketComText = (TicketComText) ticketCom;
                int height = ticketComText.getStartLineHeightMax(graphics2D, startX);
                setNextComStartY(getNextComStartY() + height);
            }
        }

        for (int i = 0; i < ticketComs.size(); i++) {
            Color color = graphics2D.getColor();
            Font font = graphics2D.getFont();

            ticketComs.get(i).drawCom(graphics2D);


            graphics2D.setColor(color);
            graphics2D.setFont(font);
        }
        yMax = getNextComStartY() + paddingBottom;
    }

    private TicketCom selectTicketCom;

    public TicketCom getSelectTicketCom() {
        return selectTicketCom;
    }

    public void selectTicketCom(int index) {
        synchronized (TicketDesign.class) {
            if (ticketComs.size() > index) {
                selectTicketCom = ticketComs.get(index);

            }
        }
    }

    protected JSONObject getTicketComTextJSONObject(TicketComText ticketComText) {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("ComType", ticketComText.getComType());
        jSONObject.put("Name", ticketComText.getName());
        jSONObject.put("Size", ticketComText.getSize());
        jSONObject.put("SampleContent", ticketComText.getSampleContent());
        jSONObject.put("FrontEnterNum", ticketComText.getFrontEnterNum());
        jSONObject.put("FrontLen", ticketComText.getFrontLen());
        jSONObject.put("BehindEnterNum", ticketComText.getBehindEnterNum());
        jSONObject.put("BehindLen", ticketComText.getBehindLen());

        return jSONObject;
    }

    protected TicketComText getTicketComTextFromJSONObject(JSONObject jSONObject) {
        TicketComText ticketComText = new TicketComText();
        ticketComText.setName(jSONObject.getString("Name"));
        ticketComText.setSize(jSONObject.getInteger("Size"));
        ticketComText.setSampleContent(jSONObject.getString("SampleContent"));
        ticketComText.setFrontEnterNum(jSONObject.getInteger("FrontEnterNum"));
        ticketComText.setFrontLen(jSONObject.getInteger("FrontLen"));
        ticketComText.setBehindEnterNum(jSONObject.getInteger("BehindEnterNum"));
        ticketComText.setBehindLen(jSONObject.getInteger("BehindLen"));
        return ticketComText;
    }

    protected JSONObject getTicketComLineJSONObject(TicketComLine ticketComLine) {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("ComType", ticketComLine.getComType());
        jSONObject.put("Name", ticketComLine.getName());
        jSONObject.put("Size", ticketComLine.getSize());
        jSONObject.put("FrontEnterNum", ticketComLine.getFrontEnterNum());
        jSONObject.put("BehindEnterNum", ticketComLine.getBehindEnterNum());
        return jSONObject;
    }

    protected TicketComLine getTicketComLineFromJSONObject(JSONObject jSONObject) {
        TicketComLine ticketComLine = new TicketComLine();
        ticketComLine.setName(jSONObject.getString("Name"));
        ticketComLine.setSize(jSONObject.getInteger("Size"));
        ticketComLine.setFrontEnterNum(jSONObject.getInteger("FrontEnterNum"));
        ticketComLine.setBehindEnterNum(jSONObject.getInteger("BehindEnterNum"));
        return ticketComLine;
    }

    protected JSONObject getTicketComQrCodeJSONObject(TicketComQrCode ticketComQrCode) {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("ComType", ticketComQrCode.getComType());
        jSONObject.put("Name", ticketComQrCode.getName());
        jSONObject.put("Size", ticketComQrCode.getSize());
        jSONObject.put("Content", ticketComQrCode.getContent());
        jSONObject.put("FrontEnterNum", ticketComQrCode.getFrontEnterNum());
        jSONObject.put("BehindEnterNum", ticketComQrCode.getBehindEnterNum());

        return jSONObject;
    }


    protected TicketComQrCode getTicketComQrCodeFromJSONObject(JSONObject jSONObject) {
        TicketComQrCode ticketComQrCode = new TicketComQrCode();
        ticketComQrCode.setName(jSONObject.getString("Name"));
        ticketComQrCode.setSize(jSONObject.getInteger("Size"));
        ticketComQrCode.setFrontEnterNum(jSONObject.getInteger("FrontEnterNum"));
        ticketComQrCode.setBehindEnterNum(jSONObject.getInteger("BehindEnterNum"));
        ticketComQrCode.setContent(jSONObject.getString("Content"));
        return ticketComQrCode;
    }

    protected TicketComQrCode2 getTicketComQrCode2FromJSONObject(JSONObject jSONObject) {
        TicketComQrCode2 ticketComQrCode2 = new TicketComQrCode2();
        ticketComQrCode2.setName(jSONObject.getString("Name"));
        ticketComQrCode2.setFrontEnterNum(jSONObject.getInteger("FrontEnterNum"));
        ticketComQrCode2.setBehindEnterNum(jSONObject.getInteger("BehindEnterNum"));
        ticketComQrCode2.setWidth(jSONObject.getInteger("Width"));
        ticketComQrCode2.setHeight(jSONObject.getInteger("Height"));
        ticketComQrCode2.setQrWidth(jSONObject.getInteger("QrWidth"));
        ticketComQrCode2.setLeftpadding1(jSONObject.getInteger("LeftPadding1"));
        ticketComQrCode2.setLeftpadding2(jSONObject.getInteger("LeftPadding2"));
        ticketComQrCode2.setText1(jSONObject.getString("Text1"));
        ticketComQrCode2.setText2(jSONObject.getString("Text2"));
        return ticketComQrCode2;
    }

    protected JSONObject getTicketComQrCode2JSONObject(TicketComQrCode2 ticketComQrCode2) {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("ComType", ticketComQrCode2.getComType());
        jSONObject.put("Name", ticketComQrCode2.getName());
        jSONObject.put("FrontEnterNum", ticketComQrCode2.getFrontEnterNum());
        jSONObject.put("BehindEnterNum", ticketComQrCode2.getBehindEnterNum());
        jSONObject.put("Width", ticketComQrCode2.getWidth());
        jSONObject.put("Height", ticketComQrCode2.getHeight());
        jSONObject.put("QrWidth", ticketComQrCode2.getQrWidth());
        jSONObject.put("LeftPadding1", ticketComQrCode2.getLeftpadding1());
        jSONObject.put("LeftPadding2", ticketComQrCode2.getLeftpadding2());
        jSONObject.put("Text1", ticketComQrCode2.getText1());
        jSONObject.put("Text2", ticketComQrCode2.getText2());

        return jSONObject;
    }

    protected JSONObject getTicketComTableJSONObject(TicketComTable ticketComTable) {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("ComType", ticketComTable.getComType());
        jSONObject.put("Name", ticketComTable.getName());
        jSONObject.put("Size", ticketComTable.getSize());
        jSONObject.put("FrontEnterNum", ticketComTable.getFrontEnterNum());
        jSONObject.put("BehindEnterNum", ticketComTable.getBehindEnterNum());
        Object[][] columns = ticketComTable.getColumns();
        JSONArray columnNames = new JSONArray();
        JSONArray columnWidths = new JSONArray();
        JSONArray columnAligns = new JSONArray();
        for (Object[] column : columns) {
            columnNames.add(column[0]);
            columnWidths.add(((JComboBoxItemInt) column[1]).getValue());
            columnAligns.add(((JComboBoxItemInt) column[2]).getValue());
        }
        jSONObject.put("columnNames", columnNames);
        jSONObject.put("columnWidths", columnWidths);
        jSONObject.put("columnAligns", columnAligns);

        String[][] datas = ticketComTable.getData();
        JSONArray jSONArrayData = new JSONArray();

        for (String[] data : datas) {
            JSONArray jSONArrayDataRow = new JSONArray();
            for (String data1 : data) {
                jSONArrayDataRow.add(data1);
            }
            jSONArrayData.add(jSONArrayDataRow);
        }
        jSONObject.put("rows", jSONArrayData);


        return jSONObject;
    }

    protected TicketComTable getTicketComTableFromJSONObject(JSONObject jSONObject) {
        TicketComTable ticketComTable = new TicketComTable();
        ticketComTable.setName(jSONObject.getString("Name"));
        ticketComTable.setSize(jSONObject.getInteger("Size"));
        ticketComTable.setFrontEnterNum(jSONObject.getInteger("FrontEnterNum"));
        ticketComTable.setBehindEnterNum(jSONObject.getInteger("BehindEnterNum"));
        JSONArray columnNames = jSONObject.getJSONArray("columnNames");
        JSONArray columnWidths = jSONObject.getJSONArray("columnWidths");
        JSONArray columnAligns = jSONObject.getJSONArray("columnAligns");
        Object[][] columns = new Object[columnNames.size()][3];
        for (int i = 0; i < columns.length; i++) {
            columns[i][0] = columnNames.getString(i);
            columns[i][1] = new JComboBoxItemInt(columnWidths.getInteger(i), "");
            columns[i][2] = new JComboBoxItemInt(columnAligns.getInteger(i), "");
        }
        ticketComTable.setColumns(columns);
        JSONArray jSONArrayData = jSONObject.getJSONArray("rows");
        String[][] datas = new String[jSONArrayData.size()][];
        for (int i = 0; i < datas.length; i++) {
            JSONArray jSONArrayDataRow = jSONArrayData.getJSONArray(i);
            String[] datasRow = new String[jSONArrayDataRow.size()];
            for (int j = 0; j < datasRow.length; j++) {
                datasRow[j] = jSONArrayDataRow.getString(j);
            }
            datas[i] = datasRow;
        }
        ticketComTable.setData(datas);

        return ticketComTable;
    }

    public JSONArray toJson() {
        JSONArray jSONArray = new JSONArray();
        for (int i = 0; i < ticketComs.size(); i++) {
            TicketCom ticketCom = ticketComs.get(i);
            if (ticketCom instanceof TicketComText) {
                jSONArray.add(getTicketComTextJSONObject((TicketComText) ticketCom));
            } else if (ticketCom instanceof TicketComLine) {
                jSONArray.add(getTicketComLineJSONObject((TicketComLine) ticketCom));
            } else if (ticketCom instanceof TicketComQrCode) {
                jSONArray.add(getTicketComQrCodeJSONObject((TicketComQrCode) ticketCom));
            } else if (ticketCom instanceof TicketComQrCode2) {
                jSONArray.add(getTicketComQrCode2JSONObject((TicketComQrCode2) ticketCom));
            } else if (ticketCom instanceof TicketComTable) {
                jSONArray.add(getTicketComTableJSONObject((TicketComTable) ticketCom));
            }
        }
        return jSONArray;
    }

    public String toJsonString() {
        return toJson().toString();
    }

    private void loadJSONArray(JSONArray jSONArray) {
        for (int i = 0; i < jSONArray.size(); i++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i);
            EnumComType comType = EnumComType.of(jSONObject.getInteger("ComType"));
            TicketCom ticketCom = null;
            if (comType == EnumComType.LINE) {
                ticketCom = getTicketComLineFromJSONObject(jSONObject);
            } else if (comType == EnumComType.QRCODE) {
                ticketCom = getTicketComQrCodeFromJSONObject(jSONObject);
            } else if (comType == EnumComType.TABLE) {
                ticketCom = getTicketComTableFromJSONObject(jSONObject);
            } else if (comType == EnumComType.TEXT) {
                ticketCom = getTicketComTextFromJSONObject(jSONObject);
            } else if (comType == EnumComType.QRCODE2) {
                ticketCom = getTicketComQrCode2FromJSONObject(jSONObject);
            }

            if (ticketCom != null) {
                // addTicketCom(ticketCom);
            }
        }
    }
}

/**
 *
 * The MIT License
 *
 * Copyright 2018, 2019 Paul Conti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package builder.models;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellEditor;

import builder.commands.PropertyCommand;
import builder.common.CommonUtils;
import builder.common.EnumFactory;
import builder.common.HexToImgConv;
import builder.controller.Controller;
import builder.events.MsgBoard;
//import builder.tables.ImageCellEditor;
import builder.prefs.GeneralEditor;

/**
 * The Class ImgButtonModel implements the model for the Image Button widget.
 * 
 * @author Paul Conti
 * 
 */
public class ImgButtonModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Property Index Constants. */
  static private final int PROP_IMAGE           =  7;
  static private final int PROP_DEFINE          =  8;
  static private final int PROP_EXTERN          =  9;
  static private final int PROP_MEMORY          = 10;
  static private final int PROP_IMAGE_SEL       = 11;
  static private final int PROP_DEFINE_SEL      = 12;
  static private final int PROP_EXTERN_SEL      = 13;
  static private final int PROP_MEMORY_SEL      = 14;
  static private final int PROP_FORMAT          = 15;
  static private final int PROP_TRANSPARENCY    = 16;
  static private final int PROP_CHANGE_PAGE     = 17;
  static private final int PROP_POPUP_SHOW      = 18;
  static private final int PROP_PAGE            = 19;
  static private final int PROP_POPUP_HIDE      = 20;
  static private final int PROP_FRAME_EN        = 21;
  static private final int PROP_FRAME_COLOR     = 22;

  /** The Property Defaults */
  static public  final String  DEF_IMAGE             = "";
  static public  final String  DEF_DEFINE            = "";
  static public  final String  DEF_EXTERN            = "";
  static public  final String  DEF_MEMORY            = "";
  static public  final String  DEF_IMAGE_SEL         = "";
  static public  final String  DEF_DEFINE_SEL        = "";
  static public  final String  DEF_EXTERN_SEL        = "";
  static public  final String  DEF_MEMORY_SEL        = "";
  static public  final String  DEF_FORMAT            = "";
  static public  final Boolean DEF_TRANSPARENCY      = Boolean.FALSE;
  static public  final Boolean DEF_CHANGE_PAGE       = Boolean.FALSE;
  static public  final Boolean DEF_POPUP_SHOW        = Boolean.FALSE;
  static public  final String  DEF_PAGE              = "";
  static public  final Boolean DEF_POPUP_HIDE        = Boolean.FALSE;
  static public  final Boolean DEF_FRAME_EN          = Boolean.FALSE;
  static public  final Color   DEF_FRAME_COLOR       = Color.WHITE;
  
  /** The general model. */
  private GeneralModel generalModel;
  
  /** The image. */
  private BufferedImage image;
  
  /** The image selected. */
  private BufferedImage imageSelected;
  
  /** The cb memory. */
  JComboBox<String> cbMemory;
  
  /** The align cell editor. */
  DefaultCellEditor memoryCellEditor;

  /** The cb format. */
  JComboBox<String> cbFormat;
  
  /** The format cell editor. */
  DefaultCellEditor formatCellEditor;

  /** memory Constants */
  public  final static String SRC_SD   = "gslc_GetImageFromSD((const char*)";
  public  final static String SRC_PROG = "gslc_GetImageFromProg((const unsigned char*)";
  public  final static String SRC_RAM  = "gslc_GetImageFromRam((unsigned char*)";
  public  final static String SRC_FILE = "gslc_GetImageFromFile(";

  /** format Constants */
  public  final static String FORMAT_BMP24  = "GSLC_IMGREF_FMT_BMP24";
  public  final static String FORMAT_BMP16  = "GSLC_IMGREF_FMT_BMP16";
  public  final static String FORMAT_RAW    = "GSLC_IMGREF_FMT_RAW";
  
  /**
   * Instantiates a new img button model.
   */
  public ImgButtonModel() {
    generalModel = (GeneralModel) GeneralEditor.getInstance().getModel();
    initProperties();
    initEditors();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.IMAGEBUTTON;
    data = new Object[23][5];
    
    initCommonProps(0, 0);
    
    imageSelected = null;
    
    // can't change height and width of image without scaling support
    data[PROP_WIDTH][PROP_VAL_READONLY]=Boolean.TRUE;
    data[PROP_HEIGHT][PROP_VAL_READONLY]=Boolean.TRUE;

    initProp(PROP_IMAGE, String.class, "IBTN-100", Boolean.TRUE,"Image",DEF_IMAGE);
    initProp(PROP_DEFINE, String.class, "IBTN-102", Boolean.FALSE,"Image #defines",DEF_DEFINE);
    initProp(PROP_EXTERN, String.class, "IBTN-108", Boolean.TRUE,"Image Extern",DEF_EXTERN);
    initProp(PROP_MEMORY, String.class, "IBTN-110", Boolean.FALSE,"Image Memory",DEF_MEMORY);

    initProp(PROP_IMAGE_SEL, String.class, "IBTN-101", Boolean.TRUE,"Image When Selected",DEF_IMAGE_SEL);
    initProp(PROP_DEFINE_SEL, String.class, "IBTN-103", Boolean.FALSE,"Image Select #defines",DEF_DEFINE_SEL);
    initProp(PROP_EXTERN_SEL, String.class, "IBTN-109", Boolean.TRUE,"Image Select Extern",DEF_EXTERN_SEL);
    initProp(PROP_MEMORY_SEL, String.class, "IBTN-113", Boolean.FALSE,"Image Select Memory",DEF_MEMORY_SEL);

    initProp(PROP_FORMAT, String.class, "IBTN-104", Boolean.FALSE,"Image Format",DEF_FORMAT);
    initProp(PROP_TRANSPARENCY, Boolean.class, "IBTN-107", Boolean.FALSE,"Transparent?",DEF_TRANSPARENCY);

    initProp(PROP_CHANGE_PAGE, Boolean.class, "TBTN-100", Boolean.FALSE,"Jump to Page?",DEF_CHANGE_PAGE);
    initProp(PROP_POPUP_SHOW, Boolean.class, "IBTN-105", Boolean.FALSE,"Show Popup Page?",DEF_POPUP_SHOW);
    initProp(PROP_PAGE, String.class, "IBNT-106", Boolean.TRUE,"Jump/Popup Page Enum",DEF_PAGE);
    initProp(PROP_POPUP_HIDE, Boolean.class, "TBTN-103", Boolean.FALSE,"Hide Popup Page?",DEF_POPUP_HIDE);

    initProp(PROP_FRAME_EN, Boolean.class, "COM-010", Boolean.FALSE,"Frame Enabled?",DEF_FRAME_EN);
    initProp(PROP_FRAME_COLOR, Color.class, "COL-302", Boolean.TRUE,"Frame Color",DEF_FRAME_COLOR);

  }

  /**
   * Initializes the comboboxes.
   */
  private void initEditors()
  {
    cbMemory = new JComboBox<String>();
    cbMemory.addItem(SRC_SD);
    cbMemory.addItem(SRC_FILE);
    cbMemory.addItem(SRC_PROG);
    cbMemory.addItem(SRC_RAM);
    memoryCellEditor = new DefaultCellEditor(cbMemory);
    cbFormat = new JComboBox<String>();
    cbFormat.addItem(FORMAT_BMP24);
    cbFormat.addItem(FORMAT_BMP16);
    cbFormat.addItem(FORMAT_RAW);
    formatCellEditor = new DefaultCellEditor(cbFormat);
  }
  
  /**
   * getEditorAt
   *
   * @see builder.models.WidgetModel#getEditorAt(int)
   */
  @Override
  public TableCellEditor getEditorAt(int rowIndex) {
    if (rowIndex == PROP_MEMORY || rowIndex == PROP_MEMORY_SEL)
      return memoryCellEditor;
    if (rowIndex == PROP_FORMAT)
      return formatCellEditor;
    return null;
  }

  /**
   * Gets the extern name.
   *
   * @return the extern name
   */
  public String getExternName() {
    return (String) data[PROP_EXTERN][PROP_VAL_VALUE];
  }
  
  /**
   * Sets the extern name.
   *
   * @param name
   *          the new extern name
   */
  public void setExternName(String name) {
    shortcutValue(name, PROP_EXTERN);
//    data[PROP_MEMORY][PROP_VAL_READONLY]=Boolean.FALSE;
    data[PROP_DEFINE][PROP_VAL_READONLY]=Boolean.TRUE;
  }

  /**
   * Gets the select extern name.
   *
   * @return the select extern name
   */
  public String getSelExternName() {
    return (String) data[PROP_EXTERN_SEL][PROP_VAL_VALUE];
  }
  
  /**
   * Sets the select extern name.
   *
   * @param name
   *          the new select extern name
   */
  public void setSelExternName(String name) {
    shortcutValue(name, PROP_EXTERN_SEL);
//    data[PROP_MEMORY_SEL][PROP_VAL_READONLY]=Boolean.FALSE;
    data[PROP_DEFINE_SEL][PROP_VAL_READONLY]=Boolean.TRUE;
  }

  /**
   * Gets the memory type.
   *
   * @return the memory type
   */
  public String getMemory() {
    return (String) data[PROP_MEMORY][PROP_VAL_VALUE];
  }
  
  /**
   * Gets the memory type.
   *
   * @return the memory type
   */
  public String getSelMemory() {
    return (String) data[PROP_MEMORY_SEL][PROP_VAL_VALUE];
  }
  
  /**
   * Sets the image name.
   *
   * @param name
   *          the new image name
   */
  public void setImageName(String name) {
    data[PROP_IMAGE][PROP_VAL_VALUE]=name;
  }

  /**
   * Gets the image format.
   *
   * @return the image format
   */
  public String getImageFormat() {
    return (String) data[PROP_FORMAT][PROP_VAL_VALUE];
  }
  
  /**
   * Sets the image format.
   *
   * @param name
   *          the new image format
   */
  public void setImageFormat(String name) {
    data[PROP_FORMAT][PROP_VAL_VALUE]=name;
  }

  /**
   * Sets the image selected name.
   *
   * @param name
   *          the new image selected name
   */
  public void setImageSelectedName(String name) {
    data[PROP_IMAGE_SEL][PROP_VAL_VALUE]=name;
  }

  /**
   * is Transparent?
   *
   * @return <code>true</code>, if successful
   */
  public boolean isTransparent() {
    return (((Boolean) data[PROP_TRANSPARENCY][PROP_VAL_VALUE]).booleanValue());
  }
  
  /**
   * Gets the frame color.
   *
   * @return the frame color
   */
  public Color getFrameColor() {
    return (((Color) data[PROP_FRAME_COLOR][PROP_VAL_VALUE]));
  }

  /**
   * changeValueAt
   *
   * @see builder.models.WidgetModel#changeValueAt(java.lang.Object, int)
   */
  @Override
  public void changeValueAt(Object value, int row) {
    // The test for Integer supports copy and paste from clipboard.
    // Otherwise we get a can't cast class String to Integer fault
    if ( (getClassAt(row) == Integer.class) && (value instanceof String)) {
        data[row][PROP_VAL_VALUE] = Integer.valueOf(Integer.parseInt((String)value));
    } else {
      data[row][PROP_VAL_VALUE] = value;
    }
    fireTableCellUpdated(row, 1);
    if (row == PROP_CHANGE_PAGE) {
      if (isChangePage()) {
        data[PROP_POPUP_SHOW][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
      } else {
        data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_SHOW][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_PAGE][PROP_VAL_VALUE]="";
        data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.FALSE;
      }
      fireTableCellUpdated(PROP_PAGE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_POPUP_SHOW, COLUMN_VALUE);
      fireTableCellUpdated(PROP_POPUP_HIDE, COLUMN_VALUE);
    }
    if (row == PROP_POPUP_SHOW) {
      if (isShowPopup()) {
        data[PROP_CHANGE_PAGE][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.TRUE;
      } else {
        data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_CHANGE_PAGE][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_PAGE][PROP_VAL_VALUE]="";
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_HIDE][PROP_VAL_VALUE]=Boolean.FALSE;
      }
      fireTableCellUpdated(PROP_PAGE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_CHANGE_PAGE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_POPUP_HIDE, COLUMN_VALUE);
    }
    if (row == PROP_POPUP_HIDE) {
      if (isHidePopup()) {
        data[PROP_CHANGE_PAGE][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_POPUP_SHOW][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.TRUE;
      } else {
        data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_CHANGE_PAGE][PROP_VAL_VALUE]=Boolean.FALSE;
        data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.FALSE;
        data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
        data[PROP_PAGE][PROP_VAL_VALUE]="";
        data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.FALSE;
      }
      fireTableCellUpdated(PROP_CHANGE_PAGE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_PAGE, COLUMN_VALUE);
      fireTableCellUpdated(PROP_POPUP_SHOW, COLUMN_VALUE);
    }
    if (row == PROP_FRAME_EN) {
      if (isFrameEnabled()) {
        data[PROP_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.FALSE;
      } else {
        data[PROP_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.TRUE;
      }
      fireTableCellUpdated(PROP_FRAME_COLOR, COLUMN_VALUE);
    }
    if (bSendEvents) {
      if (row == PROP_ENUM) {
        MsgBoard.getInstance().sendEnumChange(getKey(), getKey(), getEnum());
      } else {
        MsgBoard.getInstance().sendRepaint(getKey(),getKey());
      }
    } 
  }

  /**
   * Checks if is frame enabled.
   *
   * @return true, if is frame enabled
   */
  public boolean isFrameEnabled() {
    return ((Boolean) data[PROP_FRAME_EN][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Checks if is change page funct.
   *
   * @return true, if is change page funct
   */
  public boolean isChangePage() {
    return ((Boolean) data[PROP_CHANGE_PAGE][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Checks if is popup page funct.
   *
   * @return true, if is popup page funct
   */
  public boolean isShowPopup() {
    return ((Boolean) data[PROP_POPUP_SHOW][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Checks if is hide popup page funct.
   *
   * @return true, if is hide popup page funct
   */
  public boolean isHidePopup() {
    return ((Boolean) data[PROP_POPUP_HIDE][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Gets the change page enum.
   *
   * @return the change page enum
   */
  public String getChangePageEnum() {
    return ((String) data[PROP_PAGE][PROP_VAL_VALUE]);
  }

  /**
   * Gets the image.
   *
   * @return the image
   */
  public BufferedImage getImage() {
    return image;
  }

  /**
   * Sets the image.
   *
   * @param file
   *          the file
   * @param x
   *          the x
   * @param y
   *          the y
   */
  public void setImage(File file, int x, int y) {
    image = null;
    if (file.getName().toLowerCase().endsWith(".c")) {
      HexToImgConv convert = new HexToImgConv();
      image = convert.doConvert(file);
      if (image != null) {
        setImageFormat("GSLC_IMGREF_FMT_BMP24");
        setExternName(convert.getExternName());
        if (generalModel.getTarget().equals("linux"))
          data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_RAM;
        else      
          data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_PROG;
        setWidth(convert.getWidth());
        setHeight(convert.getHeight());
      }
    } else {
      try {
          image = ImageIO.read(file);
      } catch(IOException e) {
          System.out.println("read error: " + e.getMessage());
      }
      setWidth(image.getWidth());
      setHeight(image.getHeight());
      if (image.getType() == BufferedImage.TYPE_3BYTE_BGR)
        setImageFormat("GSLC_IMGREF_FMT_BMP24");
      else if (image.getType() == BufferedImage.TYPE_USHORT_555_RGB) 
        setImageFormat("GSLC_IMGREF_FMT_BMP16");
      else
        setImageFormat("GSLC_IMGREF_FMT_RAW1");
      if (generalModel.getTarget().equals("linux"))
        data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_FILE;
      else      
        data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_SD;
      // now construct a #define to use during code generation
      String fileName = file.getName();
      int n = fileName.indexOf(".bmp");
      if (n > 0) {
        String tmp = fileName.substring(0,n);
        fileName = tmp.toUpperCase();
      }
      // remove all special characters
      fileName = fileName.replaceAll("\\W", ""); 
      fileName = "IMG_" + fileName;
      setDefine(fileName);
      fileName = file.getName();
      setImageName(fileName);
    }
    if (getDefine() != null && !getDefine().isEmpty()) {
      data[PROP_EXTERN][PROP_VAL_READONLY]=Boolean.TRUE;
    } else {
      data[PROP_EXTERN][PROP_VAL_READONLY]=Boolean.FALSE;
    }
    if (getExternName() != null && !getExternName().isEmpty()) {
      data[PROP_DEFINE][PROP_VAL_READONLY]=Boolean.TRUE;
    } else {
      data[PROP_DEFINE][PROP_VAL_READONLY]=Boolean.FALSE;
    }
  }

  /**
   * Gets the image selected.
   *
   * @return the image selected
   */
  public BufferedImage getImageSelected() {
    return imageSelected;
  }

  /**
   * Gets the image selected file name.
   *
   * @return the image selected file name
   */
  public String getImageSelectedName() {
    return (String)data[PROP_IMAGE_SEL][PROP_VAL_VALUE];
  }

  /**
   * Sets the image selected.
   *
   * @param file
   *          the new image selected
   */
  public void setImageSelected(File file) {
    imageSelected = null;
    if (file.getName().toLowerCase().endsWith(".c")) {
      HexToImgConv convert = new HexToImgConv();
      imageSelected = convert.doConvert(file);
      if (imageSelected != null) {
        setSelExternName(convert.getExternName());
        if (generalModel.getTarget().equals("linux"))
          data[PROP_MEMORY_SEL][PROP_VAL_VALUE] = SRC_RAM;
        else      
          data[PROP_MEMORY_SEL][PROP_VAL_VALUE] = SRC_PROG;
        setWidth(convert.getWidth());
        setHeight(convert.getHeight());
      }
    } else {
      try {
        imageSelected = ImageIO.read(file);
      } catch(IOException e) {
          System.out.println("read error: " + e.getMessage());
      }
      if (generalModel.getTarget().equals("linux"))
        data[PROP_MEMORY_SEL][PROP_VAL_VALUE] = SRC_FILE;
      else      
        data[PROP_MEMORY_SEL][PROP_VAL_VALUE] = SRC_SD;
      String fileName = file.getName();
      // now construct a #define to use during code generation
      int n = fileName.indexOf(".bmp");
      if (n > 0) {
        String tmp = fileName.substring(0,n);
        fileName = tmp.toUpperCase();
      }
      // remove all special characters
      fileName = fileName.replaceAll("\\W", ""); 
      fileName = "IMG_" + fileName + "_SEL";
      setSelDefine(fileName);
      fileName = file.getName();
      setImageSelectedName(fileName);
    }
    if (getSelDefine() != null && !getSelDefine().isEmpty()) {
      data[PROP_EXTERN_SEL][PROP_VAL_READONLY]=Boolean.TRUE;
    } else {
      data[PROP_EXTERN_SEL][PROP_VAL_READONLY]=Boolean.FALSE;
    }
    if (getSelExternName() != null && !getSelExternName().isEmpty()) {
      data[PROP_DEFINE_SEL][PROP_VAL_READONLY]=Boolean.TRUE;
    } else {
      data[PROP_DEFINE_SEL][PROP_VAL_READONLY]=Boolean.FALSE;
    }
  }

  /**
   * Gets the image name.
   *
   * @return the image name
   */
  public String getImageName() {
    String dir = generalModel.getTargetImageDir();
    String name = (String) data[PROP_IMAGE][PROP_VAL_VALUE];
    // do we need to add a relative path for code generation?
    if (dir.length() > 0)
      name = dir + name;
    return name;
  }
  
  /**
   * Gets the select image name.
   *
   * @return the select image name
   */
  public String getSelectImageName() {
    String dir = generalModel.getTargetImageDir();
    String name = (String) data[PROP_IMAGE_SEL][PROP_VAL_VALUE];
    // do we need to add a relative path for code generation?
    if (dir.length() > 0)
      name = dir + name;

    return name;
  }
  
  /**
   * Gets the define.
   *
   * @return the define
   */
  public String getDefine() {
    return (String) data[PROP_DEFINE][PROP_VAL_VALUE];
  }
  
  /**
   * Sets the define.
   *
   * @param s
   *          the new define
   */
  public void setDefine(String s) {
    shortcutValue(s, PROP_DEFINE);
  }

  /**
   * Gets the sel define.
   *
   * @return the sel define
   */
  public String getSelDefine() {
    return (String) data[PROP_DEFINE_SEL][PROP_VAL_VALUE];
  }
  
  /**
   * Sets the sel define.
   *
   * @param s
   *          the new sel define
   */
  public void setSelDefine(String s) {
    shortcutValue(s, PROP_DEFINE_SEL);
  }

  /**
   * writeModel
   * @param out
   *          the out stream
   *
   * @see builder.models.WidgetModel#writeModel(java.io.ObjectOutputStream)
   */
  @Override
  public void writeModel(ObjectOutputStream out) 
      throws IOException {
    super.writeModel(out);
    out.writeObject((String)CommonUtils.getInstance().encodeToString(image));
    out.writeObject((String)CommonUtils.getInstance().encodeToString(imageSelected));
  }
  
  /**
   * readModel() will deserialize our model's data from a string object for backup
   * and recovery.
   *
   * @param in
   *          the in
   * @param widgetType
   *          the widget type
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws ClassNotFoundException
   *           the class not found exception
   * @see builder.models.WidgetModel#readModel(java.io.ObjectInputStream, java.lang.String)
   */
  @Override
  public void readModel(ObjectInputStream in, String widgetType) 
      throws IOException, ClassNotFoundException {
    super.readModel(in,  widgetType);
    String imageString = (String)in.readObject();
    image = CommonUtils.getInstance().decodeToImage(imageString);
    String imageSelectedString = (String)in.readObject();
    imageSelected = CommonUtils.getInstance().decodeToImage(imageSelectedString);
    if (isChangePage()) {
      data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.TRUE;
    } else if (isShowPopup()) {
      data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.TRUE;
    } else if (isHidePopup()) {
      data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.FALSE;
    } else {
      data[PROP_CHANGE_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_POPUP_SHOW][PROP_VAL_READONLY]=Boolean.FALSE;
      data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      data[PROP_POPUP_HIDE][PROP_VAL_READONLY]=Boolean.FALSE;
    }
    if (getDefine() != null && !getDefine().isEmpty()) {
      data[PROP_EXTERN][PROP_VAL_READONLY]=Boolean.TRUE;
    } else {
      data[PROP_EXTERN][PROP_VAL_READONLY]=Boolean.FALSE;
    }
    if (getExternName() != null && !getExternName().isEmpty()) {
      data[PROP_DEFINE][PROP_VAL_READONLY]=Boolean.TRUE;
    } else {
      data[PROP_DEFINE][PROP_VAL_READONLY]=Boolean.FALSE;
    }
    if (getSelDefine() != null && !getSelDefine().isEmpty()) {
      data[PROP_EXTERN_SEL][PROP_VAL_READONLY]=Boolean.TRUE;
    } else {
      data[PROP_EXTERN_SEL][PROP_VAL_READONLY]=Boolean.FALSE;
    }
    if (getSelExternName() != null && !getSelExternName().isEmpty()) {
      data[PROP_DEFINE_SEL][PROP_VAL_READONLY]=Boolean.TRUE;
    } else {
      data[PROP_DEFINE_SEL][PROP_VAL_READONLY]=Boolean.FALSE;
    }
    if (((String)data[PROP_MEMORY][PROP_VAL_VALUE]).equals("PROGMEM")) {
      data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_PROG;
      data[PROP_MEMORY_SEL][PROP_VAL_VALUE] = SRC_PROG;
    } else if (((String)data[PROP_MEMORY][PROP_VAL_VALUE]).equals("SRAM")) {
      data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_RAM;
      data[PROP_MEMORY_SEL][PROP_VAL_VALUE] = SRC_RAM;
    } else if (((String)data[PROP_MEMORY][PROP_VAL_VALUE]).isEmpty()) {
      data[PROP_MEMORY][PROP_VAL_VALUE] = SRC_SD;
      data[PROP_MEMORY_SEL][PROP_VAL_VALUE] = SRC_SD;
    }
    if (isFrameEnabled()) {
      data[PROP_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.FALSE;
    } else {
      data[PROP_FRAME_COLOR][PROP_VAL_READONLY]=Boolean.TRUE;
    }
  }     

}

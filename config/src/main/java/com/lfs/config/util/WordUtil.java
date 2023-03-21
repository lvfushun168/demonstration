package com.lfs.config.util;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.docx4j.Docx4J;
import org.docx4j.TraversalUtil;
import org.docx4j.XmlUtils;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.finders.ClassFinder;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.jaxb.Context;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import org.springframework.core.io.ClassPathResource;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;

/**
 * docx4j操作word文档工具类
 */
public class WordUtil {

    /**
     * 创建一个空白 Docx 文档
     *
     * @param filePath 文件路径
     */
    public void createDocx(String filePath) throws Docx4JException {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        wordMLPackage.save(new File(filePath));
    }

    /**
     * 通过路径加载 Docx 文件
     *
     * @param filePath 文件地址
     * @return WordProcessingMLPackage操作包
     */
    public static WordprocessingMLPackage loadDocx(String filePath) throws Docx4JException {
        return WordprocessingMLPackage.load(new File(filePath));
    }


    /**
     * 直接加载 Docx 文件
     *
     * @param file 文件
     * @return WordProcessingMLPackage操作包
     */
    public static WordprocessingMLPackage loadDocx(File file) throws Docx4JException {
        return WordprocessingMLPackage.load(file);
    }


    /**
     * 创建一个空白 Docx 文档
     *
     * @param outputStream 流
     */
    public void createDocx(OutputStream outputStream) throws Docx4JException {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        wordMLPackage.save(outputStream);

    }

    /**
     * 添加带样式的文本/段落
     *
     * @param wordMLPackage docx 操作包
     * @param styled        样式
     * @param conText       文本
     * @return WordProcessingMLPackage 操作包
     */
    public WordprocessingMLPackage addContext(WordprocessingMLPackage wordMLPackage, String styled, String conText) {
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText(styled, conText);
        return wordMLPackage;
    }

    /**
     * 添加文本/段落
     *
     * @param wordMLPackage docx 操作包
     * @param conText       正文
     * @return WordProcessingMLPackage 操作包
     */
    public WordprocessingMLPackage addContext(WordprocessingMLPackage wordMLPackage, String conText) {
        wordMLPackage.getMainDocumentPart().addParagraphOfText(conText);
        return wordMLPackage;
    }

    /**
     * 添加图片
     *
     * @param wordMLPackage 操作包
     * @param imagePath     图片地址
     * @return WordProcessingMLPackage 操作包
     */
    public WordprocessingMLPackage addImage(WordprocessingMLPackage wordMLPackage, String imagePath) throws Exception {
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, new File(imagePath));
        Inline inline = imagePart.createImageInline("Filename hint", "Alternative text", 1, 2, false);
        ObjectFactory factory = new ObjectFactory();
        P paragraph = factory.createP();
        R run = factory.createR();
        paragraph.getContent().add(run);
        Drawing drawing = factory.createDrawing();
        run.getContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);
        wordMLPackage.getMainDocumentPart().addObject(paragraph);
        return wordMLPackage;
    }

    /**
     * 添加空表格
     *
     * @param wordMLPackage 操作包
     * @param row           行数
     * @param col           列数
     * @return WordProcessingMLPackage 操作包
     */
    public WordprocessingMLPackage addTable(WordprocessingMLPackage wordMLPackage, int row, int col) {
        Tbl table = TblFactory.createTable(row, col, 20000 / col);
        wordMLPackage.getMainDocumentPart().addObject(table);
        return wordMLPackage;
    }

    /**
     * 添加带数据表格, 数据必须是整齐的
     * 表头为数据的 key, 表头在第一行
     *
     * @param wordMLPackage 操作包
     * @param list          数据
     * @return WordProcessingMLPackage 操作包
     */
    public WordprocessingMLPackage addTableWithDataAndTopHeader(WordprocessingMLPackage wordMLPackage, List<Map<String, String>> list) {
        Set<String> keySet = new HashSet<>(list.get(0).keySet());
        ObjectFactory factory = Context.getWmlObjectFactory();
        Tbl table = TblFactory.createTable(0, 0, 20000 / list.get(0).size());

        // 表头
        Tr tableHeader = factory.createTr();
        keySet.forEach(e -> {
            Tc tableCell = factory.createTc();
            tableCell.getContent().add(wordMLPackage.getMainDocumentPart().createParagraphOfText(e));
            tableHeader.getContent().add(tableCell);
        });
        table.getContent().add(tableHeader);

        // 数据
        list.forEach(e -> {
            Tr tableRow = factory.createTr();
            keySet.forEach(item -> {
                Tc tableCell = factory.createTc();
                tableCell.getContent().add(wordMLPackage.getMainDocumentPart().createParagraphOfText(e.get(item)));
                tableRow.getContent().add(tableCell);
            });
            table.getContent().add(tableRow);
        });
        wordMLPackage.getMainDocumentPart().addObject(table);
        return wordMLPackage;
    }

    /**
     * 添加带数据表格, 数据必须是整齐的
     * 表头为数据的 key, 表头在第一列
     *
     * @param wordMLPackage 操作包
     * @param list          数据
     * @return WordProcessingMLPackage 操作包
     */
    public WordprocessingMLPackage addTableWithDataAndLeftHeader(WordprocessingMLPackage wordMLPackage, List<Map<String, String>> list) {
        Set<String> keySet = new HashSet<>(list.get(0).keySet());
        ObjectFactory factory = Context.getWmlObjectFactory();
        Tbl table = TblFactory.createTable(0, 0, 20000 / list.get(0).size());
        keySet.forEach(e -> {
            Tr tableRow = factory.createTr();
            Tc tableHeader = factory.createTc();
            tableHeader.getContent().add(wordMLPackage.getMainDocumentPart().createParagraphOfText(e));
            tableRow.getContent().add(tableHeader);
            list.forEach(item -> {
                Tc tableCell = factory.createTc();
                tableCell.getContent().add(wordMLPackage.getMainDocumentPart().createParagraphOfText(item.get(e)));
                tableRow.getContent().add(tableCell);
            });
            table.getContent().add(tableRow);
        });
        wordMLPackage.getMainDocumentPart().addObject(table);
        return wordMLPackage;
    }

    /**
     * 保存到流
     *
     * @param wordMLPackage 操作包
     * @param outputStream  流
     * @throws Docx4JException 异常
     */
    public void save(WordprocessingMLPackage wordMLPackage, OutputStream outputStream) throws Docx4JException {
        wordMLPackage.save(outputStream);
    }

    /**
     * 保存到文件，文件必须为Docx
     *
     * @param wordMLPackage 操作包
     * @param docxPath      文件地址
     * @throws Docx4JException 异常
     */
    public void save(WordprocessingMLPackage wordMLPackage, String docxPath) throws Docx4JException {
        wordMLPackage.save(new File(docxPath));
    }

    /**
     * 获取文件中所有内容
     *
     * @param obj      JAXBElement子类
     * @param toSearch 搜索的类型
     * @return 符合搜索类型的所有对象
     */
    public List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
        List<Object> result = new ArrayList<>();
        if (obj instanceof JAXBElement)
            obj = ((JAXBElement<?>) obj).getValue();
        if (obj.getClass().equals(toSearch))
            result.add(obj);
        else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementFromObject(child, toSearch));
            }
        }
        return result;
    }

    /**
     * 加载模板并替换数据
     *
     * @param filePath 模板文件路径
     * @param data     数据属性map
     * @return 输出文件路径
     * @throws Exception 异常
     */
    public String replaceData(String filePath, Map<String, String> data, String out) throws Exception {
        // 生成文件
        String target = UUID.randomUUID().toString();
        String outPath = out + target + ".docx";

        //加载模板文件并创建Word processingMLPackage对象
        InputStream templateInputStream = new FileInputStream(filePath);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
        VariablePrepare.prepare(wordMLPackage);

        // 替换属性
        documentPart.variableReplace(data);

        OutputStream os = new FileOutputStream(outPath);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wordMLPackage.save(outputStream);
        outputStream.writeTo(os);
        os.close();
        outputStream.close();
        templateInputStream.close();
        return outPath;
    }

    /**
     * 替换模板Docx中数据和表格数据动态添加
     *
     * @param filePath      文件路径
     * @param data          全局替换属性Map
     * @param tableDataList 列表属性
     * @return 输出文件路径
     * @throws Exception 异常
     */
    public String replaceData(String filePath, Map<String, String> data, List<Map<String, Object>> tableDataList, String out) throws Exception {
        String target = UUID.randomUUID().toString();
        // 输出文件名
        String outPath = out + target + ".docx";

        // 加载模板文件并创建Word processingMLPackage对象
        InputStream templateInputStream = new FileInputStream(filePath);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);

        // 构造循环列表的数据
        ClassFinder find = new ClassFinder(Tbl.class);

        new TraversalUtil(wordMLPackage.getMainDocumentPart().getContent(), find);

        // 获取第二个表格属性
        if (find.results.size() > 0) {
            Tbl table = (Tbl) find.results.get(0);

            // 第二行约定为模板
            Tr dynamicTr = (Tr) table.getContent().get(1);

            // 获取模板行的xml数据
            String dynamicTrXml = XmlUtils.marshaltoString(dynamicTr);

            // 循环填充模板表格行数据
            for (Map<String, Object> dataMap : tableDataList) {

                Tr newTr = (Tr) XmlUtils.unmarshallFromTemplate(dynamicTrXml, dataMap);

                table.getContent().add(newTr);

            }

            // 删除模板行的占位行
            table.getContent().remove(1);
        }

        // 设置全局的变量替换
        wordMLPackage.getMainDocumentPart().variableReplace(data);

        Docx4J.save(wordMLPackage, new File(outPath));

        return outPath;
    }

    /**
     * 替换模板Docx中数据和表格数据动态添加
     *
     * @param filePath      文件路径
     * @param data          全局替换属性Map
     * @param tableDataList 列表属性
     */
    public void replaceData(String filePath, Map<String, String> data, List<Map<String, Object>> tableDataList, OutputStream out) throws Exception {
        // 加载模板文件并创建Word processingMLPackage对象
        InputStream templateInputStream = new FileInputStream(filePath);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);

        // 构造循环列表的数据
        ClassFinder find = new ClassFinder(Tbl.class);

        new TraversalUtil(wordMLPackage.getMainDocumentPart().getContent(), find);

        // 获取第一个表格属性
        if (find.results.size() > 0) {
            Tbl table = (Tbl) find.results.get(0);

            // 第二行约定为模板
            Tr dynamicTr = (Tr) table.getContent().get(1);

            // 获取模板行的xml数据
            String dynamicTrXml = XmlUtils.marshaltoString(dynamicTr);

            // 循环填充模板表格行数据
            tableDataList.forEach(e -> {
                try {
                    table.getContent().add((Tr) XmlUtils.unmarshallFromTemplate(dynamicTrXml, e));
                } catch (JAXBException jaxbException) {
                    jaxbException.printStackTrace();
                }
            });

            // 删除模板行的占位行
            table.getContent().remove(1);
        }

        // 设置全局的变量替换
        wordMLPackage.getMainDocumentPart().variableReplace(data);
        Docx4J.save(wordMLPackage, out);
        templateInputStream.close();
    }

    /**
     * 加载模板并替换数据
     *
     * @param filePath 模板文件路径
     * @param data     数据属性map
     */
    public void replaceData(String filePath, Map<String, String> data, OutputStream out) throws Exception {
        //加载模板文件并创建Word processingMLPackage对象
        InputStream templateInputStream = new FileInputStream(filePath);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
        VariablePrepare.prepare(wordMLPackage);

        // 替换属性
        documentPart.variableReplace(data);
        wordMLPackage.save(out);
        templateInputStream.close();
    }

    /**
     * word 转 pdf
     *
     * @param wordPath word文档地址
     * @return pdf地址
     */
    public String Docx2Pdf(String wordPath) {
        OutputStream os = null;
        InputStream is = null;
        //输出pdf文件路径和名称
        String pdfNoMarkPath = wordPath.substring(0, wordPath.indexOf('.')) + ".pdf";
        try {
            is = new FileInputStream(wordPath);
            WordprocessingMLPackage mlPackage = WordprocessingMLPackage.load(is);
            Mapper fontMapper = new IdentityPlusMapper();
            fontMapper.put("等线", PhysicalFonts.get("SimSun"));
            fontMapper.put("等线 Light", PhysicalFonts.get("SimSun"));
            fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
            fontMapper.put("微软雅黑", PhysicalFonts.get("Microsoft Yahei"));
            fontMapper.put("黑体", PhysicalFonts.get("SimHei"));
            fontMapper.put("楷体", PhysicalFonts.get("KaiTi"));
            fontMapper.put("宋体", PhysicalFonts.get("SimSun"));
            fontMapper.put("仿宋", PhysicalFonts.get("FangSong"));
            fontMapper.put("新宋体", PhysicalFonts.get("NSimSun"));
            fontMapper.put("宋体扩展", PhysicalFonts.get("simsun-extB"));
            fontMapper.put("仿宋_GB2312", PhysicalFonts.get("FangSong_GB2312"));
            fontMapper.put("幼圆", PhysicalFonts.get("YouYuan"));
            fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
            fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
            fontMapper.put("华文宋体", PhysicalFonts.get("STSong"));
            fontMapper.put("华文中宋", PhysicalFonts.get("STZhongsong"));
            fontMapper.put("华文琥珀", PhysicalFonts.get("STHupo"));
            fontMapper.put("华文隶书", PhysicalFonts.get("STLiti"));
            fontMapper.put("华文新魏", PhysicalFonts.get("STXinwei"));
            fontMapper.put("华文彩云", PhysicalFonts.get("STCaiyun"));
            fontMapper.put("方正姚体", PhysicalFonts.get("FZYaoti"));
            fontMapper.put("方正舒体", PhysicalFonts.get("FZShuTi"));
            fontMapper.put("华文细黑", PhysicalFonts.get("STXihei"));
            fontMapper.put("新細明體", PhysicalFonts.get("SimSun"));
            PhysicalFonts.put("PMingLiU", PhysicalFonts.get("SimSun"));            //解决宋体（正文）和宋体（标题）的乱码问题
            PhysicalFonts.put("新細明體", PhysicalFonts.get("SimSun"));
            fontMapper.put("SimSun", PhysicalFonts.get("SimSun"));             //宋体&新宋体
            mlPackage.setFontMapper(fontMapper);

            os = new FileOutputStream(pdfNoMarkPath);

            //docx4j  docx转pdf
            FOSettings foSettings = Docx4J.createFOSettings();
            foSettings.setWmlPackage(mlPackage);
            Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);

            is.close();//关闭输入流
            os.close();//关闭输出流

            return pdfNoMarkPath;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            File file = new File(wordPath);
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        }
        return "";
    }

    /**
     * 添加水印图片
     *
     * @param inPdfPath 无水印pdf路径
     * @param markPath  水印图片地址
     * @return 生成的带水印的pdf路径
     */
    public String addTextMark(String inPdfPath, String markPath) {
        PdfStamper stamp = null;
        PdfReader reader = null;
        try {
            //输出pdf带水印文件路径和名称
            String outPdfMarkPath = inPdfPath.substring(0, inPdfPath.indexOf('.')) + "水印.pdf";

            //添加水印
            reader = new PdfReader(inPdfPath, "PDF".getBytes());
            stamp = new PdfStamper(reader, new FileOutputStream(outPdfMarkPath));
            PdfContentByte under;
            int pageSize = reader.getNumberOfPages();// 原pdf文件的总页数
            //水印图片
            Image image;
            if (markPath.contains(":"))
                image = Image.getInstance(markPath);
            else {
                ClassPathResource resource = new ClassPathResource(markPath);
                image = Image.getInstance(resource.getFile().getPath());
            }
            for (int i = 1; i <= pageSize; i++) {
                under = stamp.getOverContent(i);// 水印在之前文本下
                for (int j = 0; j < 4; j++) {
                    image.setAbsolutePosition(0, j * 250 + 100);//水印位置
                    under.addImage(image);
                    image.setAbsolutePosition(200, j * 250 + 100);//水印位置
                    under.addImage(image);
                    image.setAbsolutePosition(400, j * 250 + 100);//水印位置
                    under.addImage(image);
                }
            }
            stamp.close();// 关闭
            reader.close();//关闭

            return outPdfMarkPath;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (stamp != null) {
                    stamp.close();
                }
                if (reader != null) {
                    reader.close();//关闭
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            //删除生成的无水印pdf
            File file = new File(inPdfPath);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }
        return "";
    }
}


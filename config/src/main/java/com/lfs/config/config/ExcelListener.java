package com.lfs.config.config;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellExtra;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelListener<T> extends AnalysisEventListener<Map<Integer, String>> {
    /**
     * 所有列到字段名的映射
     * */
    private Map<Integer,String> index2FieldMap ;
    /**
     * 解析出的数据
     */
    private LinkedHashMap<Integer,Map<Integer, String>> row2DataMap = new LinkedHashMap<>();
    /**
     * 合并单元格
     */
    private List<CellExtra> extraMergeInfoList = new ArrayList<>();

    public ExcelListener(){}

    public ExcelListener(Map<Integer,String> index2FieldMap){
        this.index2FieldMap = index2FieldMap ;
    }

    @Override
    public void invoke(Map<Integer, String> rowData, AnalysisContext analysisContext) {
        //读取到的每行数据,其key是以0开始的索引
        row2DataMap.put(analysisContext.readRowHolder().getRowIndex(),rowData) ;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //所有行都解析完成
        this.explainMergeData(row2DataMap, extraMergeInfoList) ;
    }

    /**
     * 某行的数据解析失败
     * */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        System.err.println("解析失败，但是继续解析下一行: " + exception.getMessage());
        // 如果是某一个单元格的转换异常 能获取到具体行号
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            System.err.println("第{}行，第{}列解析异常" + excelDataConvertException.getRowIndex() +
                    excelDataConvertException.getColumnIndex());
        }
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        switch (extra.getType()){
            case MERGE:{
                extraMergeInfoList.add(extra);
                break;
            }
            case HYPERLINK:{
                break;
            }
            case COMMENT:{
            }
            default: {
            }
        }
    }

    /**
     * 处理合并单元格
     * @param data               解析数据
     * @param extraMergeInfoList 合并单元格信息
     * @return 填充好的解析数据
     */
    private void explainMergeData(Map<Integer,Map<Integer, String>> data, List<CellExtra> extraMergeInfoList) {
        //循环所有合并单元格信息
        extraMergeInfoList.forEach(cellExtra -> {
            int firstRowIndex = cellExtra.getFirstRowIndex();
            int lastRowIndex = cellExtra.getLastRowIndex();
            int firstColumnIndex = cellExtra.getFirstColumnIndex();
            int lastColumnIndex = cellExtra.getLastColumnIndex();

            Map<Integer,String> rdata = data.get(cellExtra.getRowIndex()) ;
            String val = null ;
            if(rdata != null){
                val = rdata.get(cellExtra.getColumnIndex()) ;
            }
            //遍历每行
            Map<Integer, String> rowData = null ;
            for(int i=firstRowIndex;i<=lastRowIndex;i++){
                rowData = data.get(i) ;
                if(rowData == null){
                    continue;
                }
                for(int c = firstColumnIndex;c<=lastColumnIndex;c++){
                    rowData.put(c,val) ;
                }
            }
        });
    }

}

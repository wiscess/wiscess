package com.wiscess.exporter.word;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExportWordParameter {
	private List<IWordElement> dataList=new ArrayList<>();
	
	public void addData(IWordElement e){
		if(dataList==null){
			dataList=new ArrayList<>();
		}
		dataList.add(e);
	}
//	public void addData(String text){
//		if(dataList==null){
//			dataList=new ArrayList<>();
//		}
//		Element element=new Phrase(36,text,new PdfFont());
//		dataList.add(element);
//	}
}

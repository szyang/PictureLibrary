package com.scut.picturelibrary.utils;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * 提取关键字 待改进
 * 
 * @author 黄建斌
 * 
 */
public class KeyWordsAnalizer {
	public static List<String> getKeywords(List<String> content) {
		Reader reader = new StringReader(content.toString());
		IKSegmenter seg = new IKSegmenter(reader, true);
		Lexeme lexme;
		try {
			StringBuffer buff = new StringBuffer();
			lexme = seg.next();
			List<String> list = new ArrayList<String>();
			while (lexme != null) {
				buff.append(lexme.getLexemeType() + ":");
				buff.append(lexme.getLexemeText() + "  \n");
				if (lexme.getLexemeType() == 4 || lexme.getLexemeType() == 1)
					list.add(lexme.getLexemeText());
				lexme = seg.next();
			}
			return doList(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static ArrayList<String> doList(List<String> lists) {
		Collections.sort(lists);

		TreeSet<String> set = new TreeSet<String>();
		for (int i = 0; i < lists.size(); i++) {
			set.add(lists.get(i));
		}
		int max = 0;
		String maxString = "";
		ArrayList<String> maxlist = new ArrayList<String>();

		Iterator<String> its = set.iterator();
		while (its.hasNext()) {
			String os = (String) its.next();
			int value = getValue(lists, os);
			if (value > max) {
				max = value;
				maxString = os;
				maxlist.add(os);
			} else if (value == max) {
				maxlist.add(os);
			}
		}

		int index = 0;
		for (int i = 0; i < maxlist.size(); i++) {
			if (maxlist.get(i).equals(maxString)) {
				index = i;
				break;
			}
		}
		ArrayList<String> resultlist = new ArrayList<String>();
		for (int i = index; i < maxlist.size(); i++) {
			resultlist.add(maxlist.get(i));
		}
		return resultlist;
	}

	public static int getValue(List<String> input, String s) {
		int time = 0;
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).equals(s)) {
				time++;
			}
		}
		return time;
	}
}

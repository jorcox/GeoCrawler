/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nutch.indexwriter.ogc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.nutch.indexer.IndexWriter;
import org.apache.nutch.indexer.IndexerMapReduce;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.indexer.NutchField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OgcIndexWriter. This pluggable indexer writes
 */
public class OgcIndexWriter implements IndexWriter {
	public static final Logger LOG = LoggerFactory.getLogger(OgcIndexWriter.class);
	private final static String RAW_CONTENT = "raw_content";
	private Configuration config;
	private String path = "out/";

	public void open(JobConf job, String name) throws IOException {
	}

	@Override
	public void delete(String key) throws IOException {
	}

	@Override
	public void update(NutchDocument doc) throws IOException {
	}

	@Override
	public void write(NutchDocument doc) throws IOException {
		System.out.println("Writing " + doc.getField("url").toString());
		NutchField raw = doc.getField(RAW_CONTENT);
		
		try {

			String content = doc.getField(RAW_CONTENT).toString();
			content = content.substring(1, content.length()-1);
			
			String url = doc.getField("url").toString();
			url = formatUrl(url);

			File file = new File(path + url);
			
			/* If file doesn't exists, then create it */
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String formatUrl(String url) {
		String out = new String(url);
		out = out.substring(8,out.length()-1);
		out = out.replace("/", "_");		
		return out;
	}

	public void close() throws IOException {

	}

	@Override
	public void commit() throws IOException {

	}

	@Override
	public Configuration getConf() {
		return config;
	}

	@Override
	public void setConf(Configuration conf) {
		config = conf;
		path = conf.get("ogc.path");
	}

	public String describe() {
		StringBuffer sb = new StringBuffer("OgcIndexWriter");
		return sb.toString();
	}
}

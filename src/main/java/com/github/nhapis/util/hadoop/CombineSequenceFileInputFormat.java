package com.github.nhapis.util.hadoop;


import java.io.IOException;

import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.CombineFileRecordReader;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;

public class CombineSequenceFileInputFormat<K, V> extends CombineFileInputFormat<K, V> {
	org.apache.hadoop.mapreduce.lib.input.TextInputFormat f = new org.apache.hadoop.mapreduce.lib.input.TextInputFormat();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RecordReader<K, V> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException {
	    return new CombineFileRecordReader((CombineFileSplit)split, context, CombineSequenceFileRecordReader.class);
	}

}

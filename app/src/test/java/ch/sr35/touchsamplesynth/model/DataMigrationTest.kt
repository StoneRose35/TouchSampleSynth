package ch.sr35.touchsamplesynth.model

import com.google.gson.JsonParser
import org.junit.Assert
import org.junit.Test

class DataMigrationTest {

    @Test
    fun testJsonEquality()
    {
        val json1 = "{\"a\": 100,\"b\":\"b is good\",\"c\": {\"nomore\": \"cowbell\",\"butmore\": \"harmonica\"}}"
        val json2 = "{\"b\":\"b is good\",\"a\": 100,\"c\": {\"butmore\": \"harmonica\",\"nomore\": \"cowbell\"}}"
        Assert.assertTrue(JsonComparator.compareJsonObject(JsonParser.parseString(json1),JsonParser.parseString(json2)))
        Assert.assertTrue(JsonComparator.compareJsonObject(JsonParser.parseString(json1),JsonParser.parseString(json1)))
    }

    @Test
    fun testJsonEquality2()
    {
        val json1 = "{\"a\": 100,\"b\":\"b is good\",\"c\": [\"nomore\", \"cowbell\",\"butmore\", \"harmonica\"]}"
        val json2 = "{\"b\":\"b is good\",\"a\": 100,\"c\": [\"butmore\", \"harmonica\",\"nomore\", \"cowbell\"]}"
        Assert.assertTrue(JsonComparator.compareJsonObject(JsonParser.parseString(json1),JsonParser.parseString(json2)))
    }

    @Test
    fun testJsonInequality2()
    {
        val json1 = "{\"a\": 200,\"b\":\"b is good\",\"c\": [\"nomore\", \"cowbell\",\"butmore\", \"harmonica\"]}"
        val json2 = "{\"b\":\"b is good\",\"a\": 100,\"c\": [\"butmore\", \"harmonica\",\"nomore\", \"cowbell\"]}"
        Assert.assertFalse(JsonComparator.compareJsonObject(JsonParser.parseString(json1),JsonParser.parseString(json2)))
    }

    @Test
    fun testJsonInequality()
    {
        val json1 = "{\"a\": 200,\"b\":\"b is good\",\"c\": [\"nomore\", \"cowbell\",\"butmore\", \"harmonica\", 13]}"
        val json2 = "{\"b\":\"b is good\",\"a\": 100,\"c\": [\"butmore\", \"harmonica\",\"nomore\", \"cowbell\"]}"
        Assert.assertFalse(JsonComparator.compareJsonObject(JsonParser.parseString(json1),JsonParser.parseString(json2)))
    }



}
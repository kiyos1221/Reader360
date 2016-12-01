package com.a360ground.epubreader360.EpubManipulation;

import android.content.Context;
import android.util.Log;

import com.a360ground.epubreader360.EpubManipulation.Archieve.ZipUtil;
import com.a360ground.epubreader360.EpubManipulation.Encryption.Encryption;
import com.a360ground.epubreader360.EpubManipulation.Model.Spine;
import com.a360ground.epubreader360.EpubManipulation.Model.TableOfContent;
import com.a360ground.epubreader360.EpubManipulation.SAXParsers.SpineSAXParser;
import com.a360ground.epubreader360.EpubManipulation.SAXParsers.TocSAXParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Kiyos Solomon on 10/31/2016.
 */
public class EpubReader {
    File filePath;
    Context context;
    File epubFolder;

    public EpubReader(File filePath, Context context) {
        this.filePath = filePath;
        this.context = context;
        epubFolder = context.getDir(filePath.getName().substring(0, filePath.getName().length() - 5), Context.MODE_PRIVATE);
    }

    public boolean unPackEpub() {
        try {
            if (filePath.getAbsolutePath().substring(filePath.getAbsolutePath().lastIndexOf("."), filePath.getAbsolutePath().length()).equalsIgnoreCase(".lomi")) {
                Encryption encryption = new Encryption(context);
                InputStream inputStream = encryption.decrypt(filePath);
                ZipUtil.unzipAll(filePath.getName().substring(0, filePath.getName().length() - 5), inputStream, context);
            } else {
                Log.d("File belongs here",filePath.getAbsolutePath());
                InputStream inputStream = new FileInputStream(filePath.getAbsolutePath());
                ZipUtil.unzipAll(filePath.getName().substring(0, filePath.getName().length() - 5), inputStream, context);
                listf(epubFolder.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
    public List<File> listf(String directoryName) {
        File directory = new File(directoryName);
        List<File> resultList = new ArrayList<File>();
        // get all the files from a directory
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }
        //System.out.println(fList);
        return resultList;
    }
    public String getContentData() throws ParserConfigurationException, SAXException, IOException {
        File file = new File(getContainerXMLPath());
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
            line = bufferedReader.readLine();
        }
        return stringBuilder.toString();
    }

    public String getContainerXMLPath() throws IOException, SAXException, ParserConfigurationException {
        StringBuilder sb = new StringBuilder();
        String container_path = epubFolder.getAbsolutePath() + File.separator;
        try {
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
            String path = context.getFilesDir().getAbsolutePath().substring(0, context.getFilesDir().getAbsolutePath().length() - 5) + "app_" + filePath.getName().substring(0, filePath.getName().length() - 5) + File.separator + "META-INF" + File.separator + "container.xml";;
            FileInputStream fis = new FileInputStream(new File(path));
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            xmlPullParser.setInput(new StringReader(sb.toString()));
            while (!"rootfile".equals(xmlPullParser.getName()) && xmlPullParser.getEventType() != XmlPullParser.END_TAG) {
                xmlPullParser.next();
            }
            container_path += xmlPullParser.getAttributeValue(null, "full-path");
        } catch (FileNotFoundException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (IOException e) {
            return "";
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return container_path;

    }

    public String getChapterContent(int position) throws ParserConfigurationException, SAXException, IOException {
        File file = new File(getChapterURL(position));
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
            line = bufferedReader.readLine();
        }
        return stringBuilder.toString();
    }

    public String getBaseURL() throws ParserConfigurationException, SAXException, IOException {
        return "file://" + new File(getChapterURL(0)).getParent() + File.separator;
    }

    public String getChapterURL(int position) throws ParserConfigurationException, SAXException, IOException {
        String container_path = getContainerXMLPath();
        String URL =getChapters().get(position).getUrl();
        String url = (URL != null) ? URL : getChapters().get(position).getAnchor();
        String chapterPath = new File(container_path).getParent() + File.separator + url.replaceAll("%20", " ");
        Log.d("Chapter Path",chapterPath);
        return chapterPath;
    }

    public TableOfContent getTableOfContent() throws IOException, SAXException, ParserConfigurationException {
        String container_path = getContainerXMLPath();
        String tocPath = new File(container_path).getParent() + File.separator + "toc.ncx";
        TocSAXParser parser = new TocSAXParser();
        return parser.getTableOfContents(tocPath);
    }

    public String getChapterName(int position) throws ParserConfigurationException, SAXException, IOException {
        return getChapters().get(position).getTitle();
    }

    public List<TableOfContent.Chapter> getChapters() throws IOException, SAXException, ParserConfigurationException {
        return getTableOfContent().getChapterList();
    }

    public List<Spine> getSpineList() throws ParserConfigurationException, SAXException, IOException {
        List<Spine> spines = new ArrayList<>();
        SpineSAXParser spineSAXParser = new SpineSAXParser();
        SAXParserFactory saxPF = SAXParserFactory.newInstance();
        SAXParser saxP = saxPF.newSAXParser();
        XMLReader xmlR = saxP.getXMLReader();
        xmlR.setContentHandler(spineSAXParser);
        xmlR.parse(getContentData());
        Spine spine;
        spine = SpineSAXParser.spine;
        spines.add(spine);
        return spines;
    }

    public String getString(InputStream i) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = i.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

    public String getAllBodies() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (int i = 0; i < getChapters().size(); i++) {
                stringBuilder.append("<div class='chapter-holder'>");
                Document doc = Jsoup.parse(getChapterContent(i));
                Elements body = doc.select("body").first().children();
                String b = body.toString();
                stringBuilder.append(b);
                stringBuilder.append("</div>");
            }
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public String getBodyAt(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Document doc = Jsoup.parse(getChapterContent(i));
            Elements body = doc.select("body");
            String b = body.toString();
            stringBuilder.append(b);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public String getReader() throws IOException {
        InputStream inputStream = null;
        String reader = null;
        try {
            Log.d("Here","I am here");
            inputStream = context.getAssets().open("reader.html");
                reader = getString(inputStream).replace("replaceme", getAllBodies());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }
}

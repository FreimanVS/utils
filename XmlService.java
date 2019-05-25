package tv.twitch.totallybot.todete.a2;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class XmlService {
    public static String getValue(String xmlString, String field) {
        String res = null;
        try (StaxStreamProcessor processor = new StaxStreamProcessor(new ByteArrayInputStream(xmlString.getBytes("UTF-8")))) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {       // while not end of XML
                int event = reader.next();   // read next event
                if (event == XMLEvent.START_ELEMENT &&
                        field.equals(reader.getLocalName())) {
                    res = reader.getElementText();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private static class StaxStreamProcessor implements AutoCloseable {
        private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

        private final XMLStreamReader reader;

        public StaxStreamProcessor(InputStream is) throws XMLStreamException {
            reader = FACTORY.createXMLStreamReader(is);
        }

        public XMLStreamReader getReader() {
            return reader;
        }

        @Override
        public void close() {
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException e) { // empty
                }
            }
        }
    }
}

package edu.umd.lib.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/****
 * Process the file to add to Solr
 *
 * @author rameshb
 *
 */
public class BoxUploadProcessor implements Processor {

  public void process(Exchange exchange) throws Exception {

    String file_name = exchange.getIn().getHeader("item_name", String.class);
    String file_ID = exchange.getIn().getHeader("item_id", String.class);
    String file_type = exchange.getIn().getHeader("item_type", String.class);

    System.out.println("File Name : " + file_name);
    System.out.println("File ID : " + file_ID);
    System.out.println("File Type : " + file_type);
  }

}
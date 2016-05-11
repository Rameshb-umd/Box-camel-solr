package edu.umd.lib.routes;

import edu.umd.lib.process.BoxDeletedProcessor;
import edu.umd.lib.process.BoxUploadProcessor;
import edu.umd.lib.process.BoxWebHookProcessor;

public class BoxListener extends AbstractRoute {

  /**
   * Initializes a new instance of this class which defines a Camel route which
   * listens for incoming service invocations.
   */
  public BoxListener() {
    // sets the name of this bean
    this.setName("{{box.routeName}}");
    // defines the service-name as set in the properties file
    this.setServiceName("{{box.serviceName}}");

  }

  @Override
  protected void defineRoute() throws Exception {

    /**
     * A generic error handler (specific to this RouteBuilder)
     */
    onException(Exception.class)
        .maximumRedeliveries("{{error.maxRedeliveries}}")
        .log("Index Routing Error: ${routeId}");

    /**
     * Parse Request from WuFoo Web hooks and create hash map for SysAid Route
     */
    from("jetty:" + this.getEndpoint()).streamCaching()
        .routeId("BoxListener")
        .process(new BoxWebHookProcessor())
        .log("Wufoo Process Completed")
        .to("direct:route.events");

    /**
     * Route Based on Event Types
     */
    from("direct:route.events")
        .routeId("Event Router")
        .choice()
        .when(header("event_type").isEqualTo("uploaded"))
        .to("direct:uploaded.box")
        .when(header("event_type").isEqualTo("deleted"))
        .to("direct:deleted.box")
        .otherwise()
        .to("direct:default.box");

    /**
     * Event Listener when File is Uploaded
     */
    from("direct:uploaded.box")
        .routeId("UploadProcessor")
        .process(new BoxUploadProcessor())
        .log("A file is Uploaded");

    /**
     * Event Listener when File is Deleted
     */
    from("direct:deleted.box")
        .routeId("DeletedProcessor")
        .process(new BoxDeletedProcessor())
        .log("A file is Deleted");

    /**
     * Default File Listener
     */
    from("direct:default.box")
        .routeId("DefaultProcessor")
        .log("Default Event");

  }

}

package org.jkm.springintegration.datatransfer;


import org.jkm.springintegration.datatransfer.component.InputMessagingGateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.annotation.IntegrationComponentScan;

/**
 * Bootstrap class for the data transfer application.
 */
@SpringBootApplication
@IntegrationComponentScan
public class SpringIntegrationDatatransferApplication {
  /**
   * Start the data transfer workflow.
   * @param args command line arguments.
   */
  private void exec() {
    // final ConfigurableApplicationContext applicationContext =
    ConfigurableApplicationContext applicationContext = SpringApplication.run
        (SpringIntegrationDatatransferApplication.class);
    // TODO --> Need to remove the parameter to invoke.
    // (applicationContext.getBean(InputMessagingGateway.class)).invokeDataTransfer(1);
    // (applicationContext.getBean(InputMessagingGateway.class)).invokeTwitter("#javaone");
//    List<DatabaseTableMetadata> output = (applicationContext.getBean(InputMessagingGateway.class)).invokeDatabase
//        ("");
//    output.forEach(f -> System.out.println(f));


    String output = (applicationContext.getBean(InputMessagingGateway.class)).invokeDatabase("");

    System.out.println("===================== " + output);

    applicationContext.registerShutdownHook();
    System.exit(0);
  }

  /**
   * Main method for the bootstrap class.
   *
   * @param args command line arguments.
   */
  public static void main(final String... args) {
    final SpringIntegrationDatatransferApplication datatransferApplication = new
        SpringIntegrationDatatransferApplication();
    datatransferApplication.exec();
  }
}

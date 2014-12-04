import gov.nih.nlm.uts.webservice.content.AtomClusterDTO;
import gov.nih.nlm.uts.webservice.content.ConceptDTO;
//import UtsMetathesaurusContent.*;
//import UtsSecurity.*;
import gov.nih.nlm.uts.webservice.content.UtsWsContentController;
import gov.nih.nlm.uts.webservice.content.UtsWsContentControllerImplService;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataController;
import gov.nih.nlm.uts.webservice.metadata.UtsWsMetadataControllerImplService;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityController;
import gov.nih.nlm.uts.webservice.security.UtsWsSecurityControllerImplService;
import gov.nih.nlm.uts.webservice.finder.UiLabel;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderController;
import gov.nih.nlm.uts.webservice.finder.UtsWsFinderControllerImplService;
import gov.nih.nlm.uts.webservice.finder.Psf;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by PMW3 on 12/2/14.
 */
public class Client {
    public static void main (String[] args) throws IOException{
        try {
            String result = "";
            Properties prop = new Properties();
            String propFileName = "properties.properties";

            InputStream inputStream = new FileInputStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            // Runtime properties
            //String username = args[0];
            String username = prop.getProperty("user");
            //String password = args[1];
            String password = prop.getProperty("password");
            //String umlsRelease = args[2];
            String umlsRelease = "2.0";
            String serviceName = "http://umlsks.nlm.nih.gov";

            UtsWsMetadataController utsMetaController = (new UtsWsMetadataControllerImplService()).getUtsWsMetadataControllerImplPort();


            UtsWsContentController utsContentService = (new UtsWsContentControllerImplService()).getUtsWsContentControllerImplPort();
            UtsWsSecurityController securityService = (new UtsWsSecurityControllerImplService()).getUtsWsSecurityControllerImplPort();

            //get the Proxy Grant Ticket - this is good for 8 hours and is needed to generate single use tickets.
            String ticketGrantingTicket = securityService.getProxyGrantTicket(username, password);

            //build some ConceptDTOs and retrieve UI and Default Preferred Name

            //use the Proxy Grant Ticket to get a Single Use Ticket
            String singleUseTicket0 = securityService.getProxyTicket(ticketGrantingTicket, serviceName);

            umlsRelease = utsMetaController.getCurrentUMLSVersion(singleUseTicket0);
            //System.out.println(umlsRelease);
            String singleUseTicket1 = securityService.getProxyTicket(ticketGrantingTicket, serviceName);

            //ConceptDTO result1 =  utsContentService.getConcept(singleUseTicket1, umlsRelease, "C0018787");
            ConceptDTO result1 =  utsContentService.getConcept(singleUseTicket1, umlsRelease, "C0018787");



            System.out.println(result1);
            System.out.println(result1.getUi() );
            System.out.println(result1.getDefaultPreferredName() );
            System.out.println(result1.getSemanticTypes() );

            //use the Proxy Grant Ticket to get another Single Use Ticket
            String singleUseTicket2 = securityService.getProxyTicket(ticketGrantingTicket, serviceName);
            ConceptDTO result2 =  utsContentService.getConcept(singleUseTicket2, umlsRelease, "C1862939");
            System.out.println(result2.getUi() );
            System.out.println(result2.getDefaultPreferredName() );
            System.out.println(result2.getAtomCount() );
            //AtomClusterDTO result3 = utsContentService.getAtomConceptRelations()


            String singleUseTicket3 = securityService.getProxyTicket(ticketGrantingTicket, serviceName);

            UtsWsFinderController UtsFinderService = (new UtsWsFinderControllerImplService()).getUtsWsFinderControllerImplPort();

            Psf myPsf = new Psf();
            myPsf.setPageLn(50);
            List<UiLabel> myUiLabels = new ArrayList<UiLabel>();


            myUiLabels = UtsFinderService.findConcepts(singleUseTicket3, umlsRelease, "atom", "lou gehrig disease", "words", myPsf);

            for (int i = 0; i < myUiLabels.size(); i++) {
                UiLabel myUiLabel = myUiLabels.get(i);
                String ui = myUiLabel.getUi();
                String label = myUiLabel.getLabel();
                System.out.print(ui);
                System.out.println(label);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

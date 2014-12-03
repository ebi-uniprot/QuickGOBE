package uk.ac.ebi.quickgo.webservice;

import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;

/**
 * @deprecated We are providing REST web services. This class would be useful
 *             in case we wanted to provide SOAP services
 * @author cbonill
 *
 */
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter{
	
	/*@Bean(name = "lookup")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema termsSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("LookUpPort");
        wsdl11Definition.setLocationUri("/ws/lookup/");
        wsdl11Definition.setTargetNamespace("http://www.ebi.ac.uk/QuickGO/ws");
        wsdl11Definition.setSchema(termsSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema termsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/lookup_soap.xsd"));
    }*/
}

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import org.json.JSONObject;

import java.util.List;

/**
 * @author Zj
 * @date 2018/11/6 17:11
 */
public class Test {
  public static void main(String[] args) {
    Fabric8 fabric8 = new Fabric8();
  // fabric8.createDeployment("tomcattest",1,"zj","tomcat","tomcat:8.5");
//  fabric8.deleteDeployment("default","test");
  // fabric8.createService("tomcattest","zj","test",8080,new IntOrString(8080));
  //fabric8.deleteService("zj","test");
   // fabric8.createPod("podtest","tomcat","tomcat:8.5","zj");
  // fabric8.deletePod("zj","podtest");
   fabric8.getNameSpace();
   // fabric8.getDeployment("zj");
  //  fabric8.editDeployment("zj","tomcattest");
  //  fabric8.createNameSpace("zj1","","");
    //fabric8.createDeployment("test",1,"default","tomcat","10.2.132.171:5000/tomcat","memory","1025Mi");
  }
}


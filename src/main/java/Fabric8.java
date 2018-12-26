/**
 * @author Zj
 * @date 2018/11/8 17:50
 */

import com.google.gson.Gson;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.extensions.DeploymentList;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static jdk.nashorn.internal.objects.NativeMath.log;
import static jdk.nashorn.internal.objects.NativeMath.min;

public class Fabric8 {

  private String master = "39.108.74.92:6443";
  private static final Logger logger = LoggerFactory.getLogger(Fabric8.class);
  private Config config = new ConfigBuilder().withMasterUrl(master).build();
  private Gson gson = new Gson();
  private KubernetesClient client = new DefaultKubernetesClient(config);

//51fd12dc-fca0-11e8-aec0-00163e08ac12

  /**
   *
   * @param deploymentName deployment
   * @param replicas 副本数
   * @param nameSpace 命名空间
   * @param images 镜像名字
   * @param imagesAddress 镜像
   * @param cpuSize cpuSize
   * @param memorySize memorySize
   * @return 是否成功
   */
  public boolean createDeployment(String deploymentName, Integer replicas,
                                    String nameSpace, String images,
                                  String imagesAddress, String cpuSize,
                                  String memorySize,String MountPath) {
    Map<String,Quantity> map = new HashMap<String, Quantity>() ;
    Map<String,Quantity> map1 = new HashMap<String, Quantity>() ;
    Quantity quantity = new Quantity();
    Quantity quantity1 = new Quantity();
    quantity.setAmount(cpuSize);
    quantity1.setAmount(memorySize);
    map.put("cpu",quantity);
    map1.put("memory",quantity1);
    try {
      Deployment deployment;
      deployment = new DeploymentBuilder()
        .withNewMetadata()
        .withName(deploymentName)
        .endMetadata()
        .withNewSpec()
        .withReplicas(replicas)
        .withNewTemplate()
        .withNewMetadata()
        .addToLabels("app", deploymentName)
        .endMetadata()
        .withNewSpec()
        .addNewContainer()
        .withName(images)
        .withImage(imagesAddress)
        .withNewResources()
        .withLimits(map)
        .withLimits(map1)
        .endResources()
        .addNewPort()
        .withContainerPort(80)
        .endPort()
        .addNewVolumeMount()
        .withMountPath(MountPath)
        .endVolumeMount()
        .endContainer()
        .endSpec()
        .endTemplate()
        .withNewSelector()
        .addToMatchLabels("app", deploymentName)
        .endSelector()
        .endSpec()
        .build();

      deployment = client.extensions().deployments().inNamespace(nameSpace).create(deployment);
      return deployment != null;
    }catch (KubernetesClientException e) {
      System.out.println("null");
      return false;
    }

  }

  /**
   *
   * @param podName podName
   * @param images 镜像名字
   * @param imagesAddress 镜像
   * @param namespace 命名空间
   * @param cpuSize cpuSize
   * @param memorySize memorySize
   * @return 是否成功
   */
  public boolean createPod(String podName, String images, String imagesAddress, String namespace,String cpuSize,String memorySize,String MountPath) {
    Quantity quantity = new Quantity();
    Quantity quantity1 = new Quantity();
    Map<String,Quantity> map = new HashMap<String, Quantity>();
    Map<String,Quantity> map1 = new HashMap<String, Quantity>();
    map.put("cpu",quantity);
    map1.put("memory",quantity1);
    try {
      Pod pod = new PodBuilder()
        .withNewMetadata()
        .withName(podName)
        .addToLabels("appname", podName)
        .endMetadata()
        .withNewSpec()
        .addNewContainer()
        .withName(images)
        .withImage(imagesAddress)
        .withNewResources()
        .withLimits(map)
        .withLimits(map1)
        .endResources()
        .addNewPort()
        .withContainerPort(80)
        .endPort()
        .addNewVolumeMount()
        .withMountPath(MountPath)
        .endVolumeMount()
        .endContainer()
        .endSpec()
        .build();

      pod = client.pods().inNamespace(namespace).create(pod);
      return pod!= null;
    }catch (KubernetesClientException e) {
      System.out.println("null");
      return false;
    }
  }

  /**
   * 删除pod
   *
   * @param nameSpace 命名空间
   * @param podName   podName
   * @return 是否成功
   */
  public boolean deletePod(String nameSpace, String podName) {
    try {
      if
      (client.pods().inNamespace(nameSpace).withName(podName).delete()) {
        System.out.println("成功删除pod");
        return true;
      }
     else {
        System.out.println("删除失败");
       return false;
      }

    } catch (KubernetesClientException e) {
      return false;
    }


  }

  /**
   * 删除deployment
   *
   * @param nameSpace      命名空间
   * @param deploymentName deploymentName
   * @return 0
   */
  public String deleteDeployment(String nameSpace, String deploymentName) {
    client.extensions().deployments().inNamespace(nameSpace).withName(deploymentName).delete();
    return "0";
  }


  /**
   * 创建service
   *
   * @param selector deployment
   * @param namespace      命名空间
   * @param serviceName    serviceName
   * @return 0
   */
  public boolean createService(String selector, String namespace, String serviceName, Integer port, IntOrString targetPort,Integer nodePort,String type) {
    try {
      Service service = new ServiceBuilder()
        .withNewMetadata()
        .withName(serviceName)
        //  .addToLabels("app",deploymentName)
        .endMetadata()
        .withNewSpec()
        .withSelector(Collections.singletonMap("app", selector))
        .addNewPort()
        .withName("test-port")
        .withProtocol("TCP")
        .withPort(port)
        .withTargetPort(targetPort)
        .withNodePort(nodePort)
        .endPort()
        .withType(type)
        .endSpec()
        .withNewStatus()
        //  .withNewLoadBalancer()
        //  .addNewIngress()
        //  .withIp(null)
        //.endIngress()
        // .endLoadBalancer()
        .endStatus()
        .build();
      service = client.services().inNamespace(namespace).create(service);
    } finally {
      client.close();
    }
 return true;
  }

  /**
   * 删除service
   *
   * @param namespace   命名空间
   * @param serviceName serviceName
   * @return 是否成功
   */
  public boolean deleteService(String namespace, String serviceName) {
    if (
      client.services().inNamespace(namespace).withName(serviceName).delete()
    ) {
      return true;
    } else {
      System.out.println("删除失败");
      return false;
    }

  }

  /**
   * 获取命名空间
   *
   * @return 命名空间
   */
  public NamespaceList getNameSpace() {
    System.out.println(client.namespaces().list());
    return client.namespaces().list();
  }

  /**
   * 获取deployment列表
   *
   * @param nameSpace 命名空间
   * @return deployment列表
   */
  public String getDeployment(String nameSpace) {
    DeploymentList deploymentList = client.extensions().deployments().list();
    return gson.toJson(deploymentList);
  }

  /**
   * 创建命名空间
   *
   * @param nameSpace 名字
   * @return true
   */
  public boolean createNameSpace(String nameSpace) {
    Namespace ns = new NamespaceBuilder()
      .withNewMetadata().withName(nameSpace)
      .addToLabels("name","")
      .addToAnnotations("","")
      .endMetadata()
      .build();
    client.namespaces().create(ns);
    return true;
  }

  /**
   * 改deployment副本数
   *
   * @param namespace      命名空间
   * @param deploymentName deploymentName
   * @param replicas       修改的副本数
   * @return 成功
   */
  public boolean editDeployment(String namespace, String deploymentName, Integer replicas) {
    try {
      client.extensions().deployments().inNamespace(namespace).withName(deploymentName).scale(replicas);
    } catch (KubernetesClientException e) {

    }
    System.out.println("副本数修改为：" + replicas);
    return true;
  }

  /**
   * 获取PodList
   *
   * @param nameSpace 命名空间
   * @return podList
   */
  public String getPodList(String nameSpace) {
    PodList podList = client.pods().inNamespace(nameSpace).list();
    return gson.toJson(podList);
  }

  /**
   * 根据podName查询pod
   *
   * @param nameSpace 命名空间
   * @param podName   podName
   * @return pod
   */
  public String getPodByPodName(String nameSpace, String podName) {
    Pod pod = client.pods().inNamespace(nameSpace).withName(podName).get();
    return gson.toJson(pod);
  }

}




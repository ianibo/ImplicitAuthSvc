package implicitauth

import grails.transaction.Transactional

import org.grails.config.PropertySourcesConfig
import org.grails.config.yaml.YamlPropertySourceLoader
import org.springframework.core.io.FileSystemResource


@Transactional
class AuthcfgService {

  def grailsApplication
  def authConf = null;

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Init looking for config in ${grailsApplication.config.authCfgFile}");

    def resource = new FileSystemResource(new File(grailsApplication.config.authCfgFile))
    def mapPropertySource = new YamlPropertySourceLoader().load( grailsApplication.config.authCfgFile, resource, null/*profile*/)
    authConf = new PropertySourcesConfig(mapPropertySource.getSource())

    log.debug("Got config: ${authConf}");
  }

  def getCfg() {
    return authConf
  }

}

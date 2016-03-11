101Crawlers README
===================

<img src="http://nutch.apache.org/assets/img/nutch_logo_tm.png" align="right" width="300" />

 - Se puede usar Ant para compilar el proyecto.
 - Se puede hacer uso de " ant eclipse " para preparar el proyecto para ser editado desde eclipse. 
 - Es necesario modificar el fichero nutch-site.xml y en la propiedad plugin.folders indicar el path a la localizacion del proyecto y añadir /build/plugins al final del path. 
   Ejemplo:

   <property>
    <name>plugin.folders</name>
    <value>/home/jorge/Escritorio/repo/build/plugins</value>
   </property>


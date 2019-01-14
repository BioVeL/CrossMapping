OpenBio CrossMapping

Directory containing the java projects for the XMap code


****************************************************************************************************************************************
To run the xmap web service in localhost using jetty and its GUI in dev mode

Requirements:
	a) Maven and java installed 
	c) To run the GUI in dev mode and not inside the gCube portal, it is required to have the portal bundle in the local machine plus a gCube symmetric key.

Steps:
    1. Checkout the crossmapping code from Subversion URL
       https://svn.cs.cf.ac.uk/OpenBio/Trunks to a local directory
       (${APP_HOME}). On Linux, using the command line tool, run
       "svn co https://svn.cs.cf.ac.uk/OpenBio/Trunks ${APP_HOME}"
	2. Open a new command terminal and change to the ${APP_HOME}\Parent directory.
	3. Run the command "mvn clean install" to install the parent project in the .m2 
	4. Change the directory to the ${APP_HOME} and there run the command "mvn clean install". 
		That will install in the ${UserHome}/.m2 all the dependencies for the xmap project
	5. Configure the jdniDataSource in ${APP_HOME}/Application/CrossMapping/src/main/webapp/WEB-INF/jetty-env.xml with the
		appropiate information to stablish the connection with XMap database.
	6. In the previous terminal window or in a new one go to the ${APP_HOME}/Application/CrossMapping 
       directory and there run the command "mvn buildnumber:create jetty:run-war -Djetty.port=8085". 
	   This will create and launch a WAR file with the cross-map web service.
	   To test that the cross-map web service  is up, connect to it with a client like soapUI
		 -http://localhost:8085/CrossMapping/XMapService?wsdl	 

	9. Once the cross-map web service has started, we can start up the GUI doing that, 
		a) Have the gCube symmetric key installed in your local machine and also the gCore portal bundle. Contact gCube (http://www2gcube.research-infrastructures.eu/index.php) to get the symmetric key.
			and see how to install the portal bundle at https://gcube.wiki.gcube-system.org/gcube/index.php/GCube_Portal_Installation. Place the symmetric key inside {$PORTAL_BUNDLE_PATH\gCore}
		b) Verify that in the file XMapGUIServiceImpl.java the stub for cross-map web service is pointing to the right end point (for development it should point to "localhost http://localhost:8085/CrossMapping/XMapService?wsdl")
		c) Open a new terminal window and go to ${APP_HOME}/Application/CrossMappingPortlet/. Once there run the command "mvn gwt:run -Denvironment=local_gCore -DgCubeCore.localPath={$PORTAL_BUNDLE_PATH\gCore}"
	    		 
****************************************************************************************************************************************
How to build a Eclipse WTP project with Maven:

Requirements: 
	a) Install Web Tool in Eclipse.
	b) Install Maven.
	c) To run the GUI in dev mode, loca access to the gCore libraries plus the gCube symmetric key would be required

Steps:
	1. Open a new command terminal and change to the ${APP_HOME}\Parent directory.
	2. Run the command "mvn clean install". to install the parent project in the .m2 
    3. Create the Eclipse WTP project files by changing the directory to the ${APP_HOME} and there run the command line: 
       "mvn eclipse:eclipse -Dwtpversion=1.5"
    4. Create in Eclipse the classpath variable M2_REPO. Go to menu Window
       -> Preferences -> Java -> Build Path -> Classpath Variables.
       Create a new M2_REPO variable, or edit the existing M2_REPO variable.
       The Path is usually %user_home%/.m2/repository (Windows) or
       ${HOME}/.m2/repository (Linux/Unix). The HOME variable must be
       expanded to its full pathname.   
    5. Import the project into your Eclipse workspace.
       Menu File -> Import... -> General -> Existing Projects into Workspace
       Click Next > and Select root directory by browsing to the ${APP_HOME}
       directory. Click Finish to import project.
	6. Configure the jdniDataSource in ${APP_HOME}/Application/CrossMapping/src/main/webapp/META-INF/context.xml with the
		appropiate information to stablish the connection with XMap database.
	7. Configure the composite file ${APP_HOME}/Application/CrossMapping/src/main/resources/XMap.composite to set the end point in which the
		cross-map web service will be deployed,
	8. Run the project CrossMapping on Tomcat.
	9. Run the GUI as a "Google web application" but first 
		a) Create a eclipse variable called GLOBUS_LOCATION to point to the directory where the PORTAL_BUNDLE_PATH\gCore folder is.
		b) Then in run configuration add to the enviroment and to the classpath the previous variable
		Note: See more info in https://gcube.wiki.gcube-system.org/gcube/index.php/Developing_gCube_Portlets_Guide
	
		
Note: If instead of a WTP project we only want to create a normal Java Project
in Eclipse run the command "mvn eclipse:eclipse"


****************************************************************************************************************************************
How to deploy into an internal mvn repository:

For each project you want to deploy, go to its folder and run "mvn install deploy:deploy"

Note: The credentials to connect to the internal repository should been set previously in .m2\settings.xml


****************************************************************************************************************************************
How to do a release:

Note: Because each project has its own lifecycle, we cann release each one independently. However if we want to release all, we need to do it one
by one, starting for Parent.

Requirements:
	a) Have all changes committed to svn
	b) Have deployed the squeleton of the mvn site

Steps:	
	1. mvn clean install release:prepare
	2. mvn release:perform

Note: To rollaback the release exectue "mvn release:rollback"

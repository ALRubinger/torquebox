package org.torquebox.rack.metadata;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

import org.jboss.vfs.VFS;


public class TorqueBoxYamlParserTest {
    
    private TorqueBoxYamlParser parser;
    private RackApplicationMetaData metadata;
    private Map<String,String> strings;
    private Map<String,Object> objects;

    @Before
    public void setUp() throws Exception {
        metadata = new RackApplicationMetaData();
        parser = new TorqueBoxYamlParser(metadata);
        strings = new HashMap<String,String>();
        objects = new HashMap<String,Object>();
    }
    
    @Test
    public void testEmptyFile() throws Exception {
        assertNull( parser.parse( VFS.getChild("") ) );
        assertEquals( metadata, parser.parse( objects ) );
    }

    @Test
    public void testEmptyWeb() throws Exception {
        assertEquals( metadata, parser.parseWeb(null) );
        parser.parseWeb(objects);
        assertEquals( Collections.EMPTY_LIST, metadata.getHosts() );
    }
        
    @Test
    public void testEmptyEnvironment() throws Exception {
        assertEquals( metadata, parser.parseEnvironment(null) );
    }

    @Test
    public void testEmptyApplication() throws Exception {
        assertEquals( metadata, parser.parseApplication(null) );
        assertEquals( metadata, parser.parseApplication(strings) );
    }

    @Test 
    public void testAbsoluteRackUpScript() throws Exception {
        strings.put("rackup", System.getProperty("user.dir") + "/src/test/resources/config.ru");
        metadata = parser.parseApplication(strings);
        assertEquals("success!\n", metadata.getRackUpScript());
    }

    @Test 
    public void testRelativeRackUpScript() throws Exception {
        strings.put("RACK_ROOT", System.getProperty("user.dir"));
        strings.put("rackup", "src/test/resources/config.ru");
        metadata = parser.parseApplication(strings);
        assertEquals("success!\n", metadata.getRackUpScript());
    }

    @Test 
    public void testLenientRootKeys() throws Exception {
        String root = "/test";
        
        if ( System.getProperty( "os.name" ).toLowerCase().matches(".*windows.*" ) ) {
        	root = "/C:" + root;
        }
        strings.put("RACK_ROOT", root);
        assertEquals( root, parser.parseApplication(strings).getRackRoot().getPathName() );
        setUp(); strings.put("RAILS_ROOT", root);
        assertEquals( root, parser.parseApplication(strings).getRackRoot().getPathName() );
        setUp(); strings.put("rack_root", root);
        assertEquals( root, parser.parseApplication(strings).getRackRoot().getPathName() );
        setUp(); strings.put("rails_root", root);
        assertEquals( root, parser.parseApplication(strings).getRackRoot().getPathName() );
        setUp(); strings.put("root", root);
        assertEquals( root, parser.parseApplication(strings).getRackRoot().getPathName() );
        setUp(); strings.put("ROOT", root);
        assertEquals( root, parser.parseApplication(strings).getRackRoot().getPathName() );
        setUp(); strings.put("RaCk_RoOt", root);
        assertEquals( root, parser.parseApplication(strings).getRackRoot().getPathName() );
    }

    @Test 
    public void testLenientEnvKeys() throws Exception {
        String env = "development";
        strings.put("RACK_ENV", env);
        assertEquals( env, parser.parseApplication(strings).getRackEnv() );
        setUp(); strings.put("RAILS_ENV", env);
        assertEquals( env, parser.parseApplication(strings).getRackEnv() );
        setUp(); strings.put("rack_env", env);
        assertEquals( env, parser.parseApplication(strings).getRackEnv() );
        setUp(); strings.put("rails_env", env);
        assertEquals( env, parser.parseApplication(strings).getRackEnv() );
        setUp(); strings.put("env", env);
        assertEquals( env, parser.parseApplication(strings).getRackEnv() );
        setUp(); strings.put("ENV", env);
        assertEquals( env, parser.parseApplication(strings).getRackEnv() );
        setUp(); strings.put("RaCk_EnV", env);
        assertEquals( env, parser.parseApplication(strings).getRackEnv() );
    }
}

package org.torquebox.rack.core;

import org.jboss.vfs.VFS;
import org.jruby.Ruby;
import org.junit.Test;
import org.torquebox.test.ruby.AbstractRubyTestCase;
import org.torquebox.rack.metadata.RackApplicationMetaData;

import static org.junit.Assert.*;

public class RackRuntimeInitializerTest extends AbstractRubyTestCase {

    @Test
    public void testInitializer() throws Exception {
        String root  = "/myapp";
        String expectedPwd = root;

        if (System.getProperty("os.name").toLowerCase().matches(".*windows.*")) {
            expectedPwd = "C:" + root;
            root = "/" + expectedPwd;
        }

        RackApplicationMetaData metadata = new RackApplicationMetaData();
        metadata.setRackRoot(VFS.getChild("/myapp"));
        metadata.setRackEnv("test");
        metadata.setContextPath("/mycontext");
        RackRuntimeInitializer initializer = new RackRuntimeInitializer(metadata);

        Ruby ruby = createRuby();
        initializer.initialize(ruby);

        String rackRoot = (String) ruby.evalScriptlet("RACK_ROOT").toJava(String.class);
        assertEquals("vfs:" + root, rackRoot);

        String rackEnv = (String) ruby.evalScriptlet("RACK_ENV").toJava(String.class);
        assertEquals("test", rackEnv);

        String pwd = (String) ruby.evalScriptlet("Dir.pwd").toJava(String.class);
        assertEquals(expectedPwd, pwd);

        String baseUri = (String) ruby.evalScriptlet("ENV['RACK_BASE_URI']").toJava(String.class);
        assertEquals("/mycontext", baseUri);
    }
}


require File.dirname(__FILE__) + '/spec_helper.rb'

describe "IO extensions for VFS" do


  before(:each) do
    @executor = java.util.concurrent::Executors.newScheduledThreadPool( 1 )
    @temp_file_provider = org.jboss.vfs::TempFileProvider.create( "vfs-test", @executor )

    @archive1_path = fix_windows_path( File.join( TEST_DATA_DIR, "/home/larry/archive1.jar" ) )
    @archive1_file = org.jboss.vfs::VFS.child( @archive1_path )
    @archive1_mount_point = org.jboss.vfs::VFS.child( @archive1_path )
    @archive1_handle = org.jboss.vfs::VFS.mountZip( @archive1_file, @archive1_mount_point, @temp_file_provider )

    @archive2_path = fix_windows_path( "#{@archive1_path}/lib/archive2.jar" )
    @archive2_file = org.jboss.vfs::VFS.child( @archive2_path )
    @archive2_mount_point = org.jboss.vfs::VFS.child( @archive2_path )
    @archive2_handle = org.jboss.vfs::VFS.mountZip( @archive2_file, @archive2_mount_point, @temp_file_provider )

    puts "archive1.path=#{@archive1_path}"
    puts "archive1.mount=#{@archive1_mount_point}"
    puts "archive2.path=#{@archive2_path}"
    puts "archive2.mount=#{@archive2_mount_point}"
  end

  after(:each) do
    @archive2_handle.close
    @archive1_handle.close
  end

  it "should allow reading of full VFS URLs" do
    content = IO.read( "vfs:#{TEST_DATA_DIR}/home/larry/archive1.jar/lib/archive2.jar/manifest.txt" ).chomp
    content.should_not be_nil
    content.should_not be_empty
    content.should eql( "This is manifest.txt" )
  end

  [ :absolute, :relative ].each do |style|
    describe "with #{style} paths" do

      case ( style )
        when :relative
          prefix = "./#{TEST_DATA_BASE}"
        when :absolute
          prefix = fix_windows_path( File.expand_path( File.join( File.dirname( __FILE__ ), '..', TEST_DATA_BASE ) ) )
      end

      it "should allow reading of regular files" do
        content = IO.read( "#{prefix}/home/larry/file1.txt" ).chomp
        content.should_not be_nil
        content.should_not be_empty
        content.should eql( "This is file 1" )
      end
    
      it "should allow reading of files within an archive" do
        puts "pwd = #{Dir.pwd}"
        puts "prefix = #{prefix}"
        content = IO.read( "#{prefix}/home/larry/archive1.jar/web.xml" ).chomp
        content.should_not be_nil
        content.should_not be_empty
        content.should eql( "This is web.xml" )
      end
    
      it "should allow reading of files within a nested archive" do
        content = IO.read( "#{prefix}/home/larry/archive1.jar/lib/archive2.jar/manifest.txt" ).chomp
        content.should_not be_nil
        content.should_not be_empty
        content.should eql( "This is manifest.txt" )
      end
    end
  end

end

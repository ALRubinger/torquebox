
require 'torquebox/logger'
require 'logger'

describe TorqueBox::Logger do

  it "should look nice for class objects" do
    require 'active_support/cache/torque_box_store'
    logger = TorqueBox::Logger.new( ActiveSupport::Cache::TorqueBoxStore )
    logger.error("JC: log for cache store")
  end

  it "should support the various boolean methods" do
    logger = TorqueBox::Logger.new
    logger.trace?.should be_false
    logger.debug?.should be_false
    logger.info?.should be_false
    logger.warn?.should be_true
    logger.error?.should be_true
    logger.fatal?.should be_true
  end

  it "should not barf on meaningless level setting" do
    logger = TorqueBox::Logger.new
    logger.level = Logger::WARN
    logger.level.should == Logger::WARN
  end

  it "should deal with blocks correctly" do 
    logger = TorqueBox::Logger.new
    logger.error "JC: message zero"
    logger.error { "JC: message" }
    logger.error "JC: message too"
  end

end


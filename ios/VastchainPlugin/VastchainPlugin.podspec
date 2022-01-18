#
# Be sure to run `pod lib lint VastchainPlugin.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see https://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'VastchainPlugin'
  s.version          = '0.1.5'
  s.summary          = 'A short description of VastchainPlugin.'

# This description is used to generate tags and improve search results.
#   * Think: What does it do? Why did you write it? What is the focus?
#   * Try to keep it short, snappy and to the point.
#   * Write the description between the DESC delimiters below.
#   * Finally, don't worry about the indent, CocoaPods strips it!

  s.description      = <<-DESC
TODO: Add long description of the pod here.
                       DESC

  # s.homepage         = 'https://vastchain.coding.net/p/patrol/d/sdk/git'                       
  s.homepage         = 'https://github.com/chenxiandiao/VastchainPlugin'
  # s.screenshots     = 'www.example.com/screenshots_1', 'www.example.com/screenshots_2'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'chenxiandiao' => '15068808239@163.com' }
  s.source           = { :git => 'git@github.com:chenxiandiao/VastchainPlugin.git', :tag => s.version.to_s }
  # s.source           = { :git => 'git@e.coding.net:vastchain/patrol/sdk.git', :tag => s.version.to_s }
  # s.social_media_url = 'https://twitter.com/<TWITTER_USERNAME>'

  s.ios.deployment_target = '9.0'

#s.source_files = 'ios/VastchainPlugin/VastchainPlugin/Classes/**/*'
#s.resource_bundles = {
#    'VastchainPlugin' => ['ios/VastchainPlugin/VastchainPlugin/Assets/*']
#}
s.source_files = 'VastchainPlugin/Classes/**/*'
s.resource_bundles = {
#    'VastchainPlugin' => ['VastchainPlugin/Assets/**/*.{storyboard,xib,xcassets,json,imageset,png}']
    'VastchainPlugin' => ['VastchainPlugin/Assets/*']
}
s.vendored_frameworks = ['VastchainPlugin/Classes/zicox_ios_sdk.framework']
#s.frameworks = ['VastchainPlugin/Classes/zicox_ios_sdk.framework']
#s.resource = ['VastchainPlugin/Assets/Face.storyboard']
  # s.public_header_files = 'Pod/Classes/**/*.h'
  # s.frameworks = 'UIKit', 'MapKit'
s.dependency 'SGQRCode', '~> 3.5.1'
end

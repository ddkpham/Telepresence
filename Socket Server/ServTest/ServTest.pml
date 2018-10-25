<?xml version="1.0" encoding="UTF-8" ?>
<Package name="ServTest" format_version="4">
    <Manifest src="manifest.xml" />
    <BehaviorDescriptions>
        <BehaviorDescription name="behavior" src="behavior_1" xar="behavior.xar" />
    </BehaviorDescriptions>
    <Dialogs>
        <Dialog name="Hello" src="Hello/Hello.dlg" />
        <Dialog name="Sport" src="Sport/Sport.dlg" />
        <Dialog name="ExampleDialog" src="behavior_1/ExampleDialog/ExampleDialog.dlg" />
        <Dialog name="PerformAction" src="PerformAction/PerformAction.dlg" />
        <Dialog name="Introduction" src="Introduction/Introduction.dlg" />
    </Dialogs>
    <Resources>
        <File name="johnmayer" src="johnmayer.wav" />
        <File name="brickhouse" src="brickhouse.wav" />
        <File name="lovesong" src="lovesong.wav" />
    </Resources>
    <Topics>
        <Topic name="Hello_enu" src="Hello/Hello_enu.top" topicName="Hello" language="en_US" />
        <Topic name="Sport_enu" src="Sport/Sport_enu.top" topicName="Sport" language="en_US" />
        <Topic name="ExampleDialog_enu" src="behavior_1/ExampleDialog/ExampleDialog_enu.top" topicName="ExampleDialog" language="en_US" />
        <Topic name="PerformAction_enu" src="PerformAction/PerformAction_enu.top" topicName="PerformAction" language="en_US" />
        <Topic name="Introduction_enu" src="Introduction/Introduction_enu.top" topicName="Introduction" language="en_US" />
    </Topics>
    <IgnoredPaths>
        <Path src=".DS_Store" />
        <Path src="Sport/Sport.dlg" />
        <Path src="Sport/Sport_enu.top" />
        <Path src="Hello/Hello_enu.top" />
        <Path src="Hello/Hello.dlg" />
        <Path src="behavior_1/behavior.xar" />
        <Path src="translations/translation_en_US.ts" />
    </IgnoredPaths>
    <Translations auto-fill="en_US">
        <Translation name="translation_en_US" src="translations/translation_en_US.ts" language="en_US" />
    </Translations>
</Package>

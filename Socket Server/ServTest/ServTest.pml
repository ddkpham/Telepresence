<?xml version="1.0" encoding="UTF-8" ?>
<Package name="ServTest" format_version="4">
    <Manifest src="manifest.xml" />
    <BehaviorDescriptions>
        <BehaviorDescription name="behavior" src="behavior_1" xar="behavior.xar" />
    </BehaviorDescriptions>
    <Dialogs>
        <Dialog name="ExampleDialog" src="behavior_1/ExampleDialog/ExampleDialog.dlg" />
        <Dialog name="PerformAction" src="PerformAction/PerformAction.dlg" />
        <Dialog name="Introduction" src="Introduction/Introduction.dlg" />
        <Dialog name="Pepper_Status" src="Pepper_Status/Pepper_Status.dlg" />
        <Dialog name="Connection" src="Connection/Connection.dlg" />
        <Dialog name="Pepper_Sensor" src="Pepper_Sensor/Pepper_Sensor.dlg" />
        <Dialog name="Installed_Applications" src="Installed_Applications/Installed_Applications.dlg" />
    </Dialogs>
    <Resources>
        <File name="johnmayer" src="johnmayer.wav" />
        <File name="brickhouse" src="brickhouse.wav" />
        <File name="lovesong" src="lovesong.wav" />
    </Resources>
    <Topics>
        <Topic name="ExampleDialog_enu" src="behavior_1/ExampleDialog/ExampleDialog_enu.top" topicName="ExampleDialog" language="en_US" />
        <Topic name="PerformAction_enu" src="PerformAction/PerformAction_enu.top" topicName="PerformAction" language="en_US" />
        <Topic name="Introduction_enu" src="Introduction/Introduction_enu.top" topicName="Introduction" language="en_US" />
        <Topic name="Pepper_Status_enu" src="Pepper_Status/Pepper_Status_enu.top" topicName="Pepper_Status" language="en_US" />
        <Topic name="Connection_enu" src="Connection/Connection_enu.top" topicName="Connection" language="en_US" />
        <Topic name="Pepper_Sensor_enu" src="Pepper_Sensor/Pepper_Sensor_enu.top" topicName="Pepper_Sensor" language="en_US" />
        <Topic name="Installed_Applications_enu" src="Installed_Applications/Installed_Applications_enu.top" topicName="Installed_Applications" language="en_US" />
    </Topics>
    <IgnoredPaths>
        <Path src="behavior_1/ExampleDialog/ExampleDialog_enu.top" />
        <Path src="PerformAction/PerformAction.dlg" />
        <Path src="johnmayer.wav" />
        <Path src="Sport/Sport.dlg" />
        <Path src="Installed_Applications" />
        <Path src="Hello/Hello_enu.top" />
        <Path src="Sport/Sport_enu.top" />
        <Path src="behavior_1/ExampleDialog/ExampleDialog.dlg" />
        <Path src="translations/translation_en_US.ts" />
        <Path src="Hello/Hello.dlg" />
        <Path src="brickhouse.wav" />
        <Path src="Installed_Applications/Installed_Applications_enu.top" />
        <Path src="Introduction/Introduction.dlg" />
        <Path src="Installed_Applications/Installed_Applications.dlg" />
        <Path src="PerformAction" />
        <Path src="Introduction" />
        <Path src="Introduction/Introduction_enu.top" />
        <Path src="lovesong.wav" />
        <Path src="PerformAction/PerformAction_enu.top" />
        <Path src="behavior_1/ExampleDialog" />
        <Path src="behavior_1/behavior.xar" />
    </IgnoredPaths>
    <Translations auto-fill="en_US">
        <Translation name="translation_en_US" src="translations/translation_en_US.ts" language="en_US" />
    </Translations>
</Package>

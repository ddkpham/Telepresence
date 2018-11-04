<?xml version="1.0" encoding="UTF-8" ?>
<Package name="ServTest" format_version="4">
    <Manifest src="manifest.xml" />
    <BehaviorDescriptions>
        <BehaviorDescription name="behavior" src="behavior_1" xar="behavior.xar" />
    </BehaviorDescriptions>
    <Dialogs>
        <Dialog name="PerformAction" src="PerformAction/PerformAction.dlg" />
        <Dialog name="Introduction" src="Introduction/Introduction.dlg" />
        <Dialog name="Data_Responses" src="Data_Responses/Data_Responses.dlg" />
        <Dialog name="Conversation" src="Conversation/Conversation.dlg" />
    </Dialogs>
    <Resources>
        <File name="johnmayer" src="johnmayer.wav" />
        <File name="brickhouse" src="brickhouse.wav" />
        <File name="lovesong" src="lovesong.wav" />
    </Resources>
    <Topics>
        <Topic name="PerformAction_enu" src="PerformAction/PerformAction_enu.top" topicName="PerformAction" language="en_US" />
        <Topic name="Introduction_enu" src="Introduction/Introduction_enu.top" topicName="Introduction" language="en_US" />
        <Topic name="Data_Responses_enu" src="Data_Responses/Data_Responses_enu.top" topicName="Data_Responses" language="en_US" />
        <Topic name="Conversation_enu" src="Conversation/Conversation_enu.top" topicName="Conversation" language="en_US" />
    </Topics>
    <IgnoredPaths>
        <Path src="PerformAction/PerformAction.dlg" />
        <Path src="Introduction/Introduction.dlg" />
        <Path src="translations/translation_en_US.ts" />
        <Path src="PerformAction" />
        <Path src="Sport/Sport_enu.top" />
        <Path src="Hello/Hello_enu.top" />
        <Path src="Hello/Hello.dlg" />
        <Path src="PerformAction/PerformAction_enu.top" />
        <Path src="lovesong.wav" />
        <Path src="Introduction" />
        <Path src="Sport/Sport.dlg" />
        <Path src="brickhouse.wav" />
        <Path src="behavior_1/behavior.xar" />
        <Path src="Introduction/Introduction_enu.top" />
        <Path src="johnmayer.wav" />
    </IgnoredPaths>
    <Translations auto-fill="en_US">
        <Translation name="translation_en_US" src="translations/translation_en_US.ts" language="en_US" />
    </Translations>
</Package>

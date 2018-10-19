<?xml version="1.0" encoding="UTF-8" ?>
<Package name="ServTest" format_version="4">
    <Manifest src="manifest.xml" />
    <BehaviorDescriptions>
        <BehaviorDescription name="behavior" src="behavior_1" xar="behavior.xar" />
    </BehaviorDescriptions>
    <Dialogs>
        <Dialog name="Hello" src="Hello/Hello.dlg" />
        <Dialog name="Sport" src="Sport/Sport.dlg" />
    </Dialogs>
    <Resources />
    <Topics>
        <Topic name="Hello_enu" src="Hello/Hello_enu.top" topicName="Hello" language="en_US" />
        <Topic name="Sport_enu" src="Sport/Sport_enu.top" topicName="Sport" language="en_US" />
    </Topics>
    <IgnoredPaths />
    <Translations auto-fill="en_US">
        <Translation name="translation_en_US" src="translations/translation_en_US.ts" language="en_US" />
    </Translations>
</Package>

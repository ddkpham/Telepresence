<?xml version="1.0" encoding="UTF-8" ?>
<Package name="ServTest" format_version="4">
    <Manifest src="manifest.xml" />
    <BehaviorDescriptions>
        <BehaviorDescription name="behavior" src="behavior_1" xar="behavior.xar" />
    </BehaviorDescriptions>
    <Dialogs>
        <Dialog name="Sport" src="Sport/Sport.dlg" />
        <Dialog name="Pepper_Status" src="Pepper_Status/Pepper_Status.dlg" />
        <Dialog name="Connection" src="Connection/Connection.dlg" />
    </Dialogs>
    <Resources />
    <Topics>
        <Topic name="Sport_enu" src="Sport/Sport_enu.top" topicName="Sport" language="en_US" />
        <Topic name="Pepper_Status_enu" src="Pepper_Status/Pepper_Status_enu.top" topicName="Pepper_Status" language="en_US" />
        <Topic name="Connection_enu" src="Connection/Connection_enu.top" topicName="Connection" language="en_US" />
    </Topics>
    <IgnoredPaths />
    <Translations auto-fill="en_US">
        <Translation name="translation_en_US" src="translations/translation_en_US.ts" language="en_US" />
    </Translations>
</Package>

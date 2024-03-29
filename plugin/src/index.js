import { withAppBuildGradle, withProjectBuildGradle, } from '@expo/config-plugins';
import { createGeneratedHeaderComment, removeGeneratedContents, } from '@expo/config-plugins/build/utils/generateCode';
const gradleMaven = [
    `allprojects { repositories { maven { url("https://artifactory-external.vkpartner.ru/artifactory/maven") } } }`,
].join('\n');
const implementationDeps = [
    `dependencies { implementation("ru.rustore.sdk:billingclient:0.1.7") }`,
].join('\n');
function addRuStoreBillingClientImportProjectGradle(src) {
    return appendContents({
        tag: 'rustore-billing-client-project',
        src,
        newSrc: gradleMaven,
        comment: '//',
    });
}
function addRuStoreBillingClientImportAppGradle(src) {
    return appendContents({
        tag: 'rustore-billing-client-app',
        src,
        newSrc: implementationDeps,
        comment: '//',
    });
}
const withRuStoreBuildGradle = (config) => {
    return withProjectBuildGradle(config, (projectFile) => {
        projectFile.modResults.contents =
            addRuStoreBillingClientImportProjectGradle(projectFile.modResults.contents).contents;
        return projectFile;
    });
};
const withRuStoreAppGradle = (config) => {
    return withAppBuildGradle(config, (projectFile) => {
        projectFile.modResults.contents = addRuStoreBillingClientImportAppGradle(projectFile.modResults.contents).contents;
        return projectFile;
    });
};
const index = (config) => {
    config = withRuStoreAppGradle(config);
    config = withRuStoreBuildGradle(config);
    return config;
};
function appendContents({ src, newSrc, tag, comment, }) {
    const header = createGeneratedHeaderComment(newSrc, tag, comment);
    if (!src.includes(header)) {
        // Ensure the old generated contents are removed.
        const sanitizedTarget = removeGeneratedContents(src, tag);
        const contentsToAdd = [
            // @something
            header,
            // contents
            newSrc,
            // @end
            `${comment} @generated end ${tag}`,
        ].join('\n');
        return {
            contents: sanitizedTarget ?? src + contentsToAdd,
            didMerge: true,
            didClear: !!sanitizedTarget,
        };
    }
    return { contents: src, didClear: false, didMerge: false };
}
export default index;

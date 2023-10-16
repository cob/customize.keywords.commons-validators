# customize.keyword.common-validators

## Install

```bash
cob-cli customize common-validators

# restart recordm
```

## How to use:

```
Ensure that the field has a valid email address:

Fields:
    field:
        name: field1
        description: $commonsValidator.email
```

For more information you can consult [this link](https://learning.cultofbits.com/docs/cob-platform/admins/managing-information/available-customizations/common-validators/)

## Build

```bash
cd others/recordm-validators
mvn clean package
cp target/cob-customize-common-validators.jar ../../recordm/bundles/
```

## Release

1. Update `costumize.js` and increment version
2. Update `pom.xml` version
3. Build

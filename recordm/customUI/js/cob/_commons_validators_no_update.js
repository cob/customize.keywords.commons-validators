cob.custom.customize.push(function(core, utils, ui) {

  const KEYWORD = "$commons.validate";

  core.customizeAllInstances(function(instance, presenter) {
    if (instance.isNew()) return;

    const userGroups = core.getGroups() || [];

    presenter.findFieldPs((fp) => {
      let validators = fp.field.fieldDefinition.configuration.extensions[KEYWORD]?.args || [];

      validators.filter(v => v.startsWith("noUpdate")).forEach(v => {
        const matches = /noUpdate\(([^)].*)\)/.exec(v);
        let groups = matches ? matches[1].split(",") : [];

        if (!groups.some(g => userGroups.includes(g))) {
          fp.disable()
        }
      });
    });
  });

});
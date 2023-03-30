import { ConfigPlugin, createRunOncePlugin } from '@expo/config-plugins';

const pkg = require('../../package.json');

const withRuStore: ConfigPlugin = (config) => {
  return config;
};

export default createRunOncePlugin(withRuStore, pkg.name, pkg.version);

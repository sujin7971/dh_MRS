const path = require('path');
const resourcesPath = 'src/main/webapp/resources';
const commonBasePath = 'front-end-assets/js/ewp/partials';
const commonConfig = {
    mode: 'production',
    resolve: {
        alias: {
            '/resources': path.resolve(__dirname, resourcesPath),
        }
    },
};

const createConfig = (entryFile) => {
    const entryName = path.basename(entryFile, '.js');
    const outputDir = path.join(path.dirname(entryFile), 'dist');

    return Object.assign({}, commonConfig, {
        entry: path.resolve(__dirname, resourcesPath, commonBasePath, entryFile),
        output: {
            filename: `${entryName}.bundle.js`,
            path: path.resolve(__dirname, resourcesPath, commonBasePath, outputDir)
        }
    });
};

const navigation_module = 'fragment/navigation.js';
const home_schedule_module = 'fragment/home/home_schedule.js';
const home_stat_module = 'fragment/home/home_stat.js';
const home_notice_module = 'fragment/home/home_notice.js'

const modules = [
    navigation_module,
	home_schedule_module,
    home_stat_module,
    home_notice_module,
].map(createConfig);

module.exports = modules;
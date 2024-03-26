const informations = require('./informations');

informations.getConnectionInfos().then(infos => {
  let b = informations.getInfos(infos[0], infos[1], [2]);
  console.log('AAA', b);
});
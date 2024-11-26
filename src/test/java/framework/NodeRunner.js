const newman = require('newman');
const fs = require('fs');
const util = require('util');
const moment = require('moment');
var argv = require('minimist')(process.argv.slice(2));
// let collection = argv['collection'];
// let enviroment = argv['enviroment'];
const path = require('path')
const delimiter = path.sep
let directorio= process.cwd()
const directory=String(directorio).replace("\\",delimiter)
const working_dir = directory+ delimiter+'src'+delimiter+'test'+delimiter+'java'+delimiter
console.log(working_dir)
const evidence_dir = directory+ delimiter+'Evidencia_Pipeline'
//const working_dir = 'C:/Respaldo/automatic-testing-apiv10/src/test/java/'


exports.TestMicroService =  function(test_name,collection, enviroment,directory,request,execution_data){
    let run_time = moment().format().toString().replace(/:/g, '_');
    if (enviroment==''){
         enviroment='';
    }else{
         enviroment=working_dir+'enviroments'+delimiter+directory +delimiter+ enviroment;
    }
    // var currentPath = process.cwd();
    // console.log(currentPath);
    newman.run({
        collection: working_dir+'collections'+delimiter+directory +delimiter+ collection,
 //       environment: working_dir+'enviroments'+delimiter+directory +delimiter+ enviroment,
        environment: enviroment,
        folder: request,
        iterationData:working_dir+'execution_data'+delimiter+execution_data,
        insecure: true,
        reporters: ['cli', 'json','htmlextra'],
        // reporters: ['json','htmlextra'],
        reporter: {
            json:{
                   export: working_dir+'logs'+delimiter+ 'response.postman_collection.log.json'
                      //   export: working_dir+'logs'+delimiter+ collection.replace('.json', '.log.json')
            },
            htmlextra: {
                export: evidence_dir+delimiter+ test_name + ' - ' + run_time + '.html',
                template: working_dir+'templates'+delimiter+'caex_report_template_extra_dashboard.hbs'
            }
        }
    }, process.exit).on('done', function (err, summary) {
        if(err || summary.error){
          console.error('collection run encountered an error.');   
        }
        else{
            fs.writeFileSync(working_dir+'logs'+delimiter+'last_report.txt',
                collection.replace('.json', '_') + run_time + '.html',
                function (err) {
                    if(err){
                        return console.log(err);
                    }
                }
            );
            console.log("Report Saved: " + test_name + ' - ' + run_time + '.html')
            return "done";
        }    
    });
};


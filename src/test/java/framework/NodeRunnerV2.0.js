const childProcess = require('child_process');

const path = require('path')
const delimiter = path.sep
let directorio= process.cwd()
const directory=String(directorio).replace("\\",delimiter)
const working_dir = directory+ delimiter+'src'+delimiter+'test'+delimiter+'java'+delimiter
console.log(working_dir)
const evidence_dir = directory+ delimiter+'Evidencia_Api'+delimiter

//const working_dir = 'C:/Respaldo/automatic-testing-apiv10/src/test/java/'


exports.TestMicroService =  function(test_name,collection, enviroment,directory,request,execution_data){

    let now= new Date();
    let mes=(now.getMonth())+1
    let dia=(now.getDate())
    if (mes<10){
        mes="0"+mes;
    }
    if (dia<10){
        dia="0"+dia;
    }
    let nameHtml= test_name+" - "+now.getFullYear()+"-"+mes+"-"+now.getDate()+"_T_"+now.getHours()+"-"+now.getMinutes()+"-"+now.getSeconds();
    if (enviroment==''){
         enviroment='';
    }else{
         enviroment=' -e '+working_dir+'enviroments'+delimiter+directory +delimiter+ enviroment;
    }
    collection= working_dir+'collections'+delimiter+directory +delimiter+ collection;
    let requestFinal=String(request).replace("'[ '", "").replace("' ]", "");
    console.log(requestFinal);
    let cantidadRequest= String(requestFinal).split(",");
    let i = 0;
    let folder=' '
    do {
        folder=folder+' --folder "'+cantidadRequest[i]+'"';
        console.log(cantidadRequest[i]);
        console.log(folder);
        i = i + 1;
    } while (cantidadRequest.length>i);
    console.log(folder);
    //console.log(summary);
    console.log('Ejecucion newman run '+collection + folder +enviroment+ ' --iteration-data '+working_dir+'execution_data'+delimiter+execution_data+
                                 ' --insecure -r htmlextra,json,csv --reporter-csv-includeBody  --reporter-htmlextra-export "'+evidence_dir+nameHtml+'.html" json --reporter-json-export "'+working_dir+'logs'+delimiter+'response.postman_collection.log.json"');
  childProcess.exec(('newman run '+collection + folder +enviroment+ ' --iteration-data '+working_dir+'execution_data'+delimiter+execution_data+
      ' --insecure -r htmlextra,json,csv --reporter-csv-includeBody  --reporter-htmlextra-export "'+evidence_dir+nameHtml+'.html" json --reporter-json-export "'+working_dir+'logs'+delimiter+'response.postman_collection.log.json" --reporter-csv-export "'+working_dir+'logs'+delimiter+'body.csv"'),  (err, stdout) => {

     if (err) {                                                                                                                                                                                                                               // threat your error here
             console.error("Se encontraron errores en el script: "+test_name+ "debido a lo siguiente "+err);
     }else {

              console.log(stdout);

     }

    })
 //console.log(child_process);

}
<#list modele as cs>
${cs.name}(${cs.ed}) : <#if (cs.percentDayChange>0) >+</#if>${cs.percentDayChange}% -> ${cs.price}
</#list>
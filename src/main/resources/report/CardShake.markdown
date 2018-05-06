<#list modele as cs>
  ${cs.name} <#if cs.ed??>(${cs.ed})</#if> ${cs.price} <#if (cs.percentDayChange>0)>+<#else>&#x25BC;</#if>${cs.percentDayChange}%
</#list>

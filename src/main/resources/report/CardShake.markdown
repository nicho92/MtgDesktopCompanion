<#list modele as cs>
**${cs.name}** <#if (cs.foil)>(Foil)</#if><#if (cs.etched)>(Etched)</#if> <#if cs.ed??>[${cs.ed}]</#if> <#if (cs.percentDayChange>0)>+</#if>${cs.percentDayChange}%  ( #{cs.price; M2} ${cs.currency} )
</#list>
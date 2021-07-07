<#list modele as insight>
**${insight.cardName}** <#if insight.ed??>[${insight.ed}]</#if> : #{insight.stock-insight.yesterdayStock}
</#list>
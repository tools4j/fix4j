<@pp.dropOutputFile />
<#list fields?keys as versionString>
<#assign version=versionString?substring(3)>
<#assign doc=fields[versionString]>

<#macro decimalTag field>
	<#assign matcher=mappings[field.Type]?matches("DecimalTag:(\\d\\d?):(\\w+)")>
	<#if matcher>
		<#assign precision=matcher?groups[1]>
		<#assign roundingMode=matcher?groups[2]>
	public static final DecimalTag ${field.Name} = new BasicDecimalTag(${field.Tag}, "${field.Type}", "${field.Name}", ${precision}, RoundingMode.${roundingMode});
	<#else>
		<#stop "Illegal type specification for DecimalTag field: expected format 'DecimalTag:<precision>:<roundingMode>' but found: " + mappings[field.Type]>
	</#if>
</#macro> 
<#macro basicTag field>
	<#assign tagType=mappings[field.Type]>
	public static final ${tagType} ${field.Name} = new Basic${tagType}(${field.Tag}, "${field.Type}", "${field.Name}");
</#macro> 
<#macro typeTag field>
	<#assign tagType=mappings[field.Type]>
	public static final ${tagType} ${field.Name} = new ${tagType}(${field.Tag}, "${field.Name}");
</#macro> 
<#macro unknownType field>
	public static final FixTag ${field.Name} = null;//No type-mapping for FIX type: ${field.Type}
</#macro> 

<@pp.changeOutputFile name=pp.home + "org/fix4j/engine/fix/" + versionString + "/FixTag" + version + ".java" />
package org.fix4j.engine.fix.${versionString};
<#list doc.Fields.Field as field>
	<#if mappings[field.Type]?? && mappings[field.Type]?starts_with("DecimalTag")>

import java.math.RoundingMode;
		<#break>
	</#if>
</#list>

import org.fix4j.engine.tag.*;
import org.fix4j.engine.tag.impl.*;
import org.fix4j.engine.tag.type.*;

public final class FixTag${version} {
<#list doc.Fields.Field as field>
	/** ${field.Description?xhtml?replace("\r\n", "<br>")?replace("\n", "<br>")} */
	<#if mappings[field.Type]??>
		<#assign tagType=mappings[field.Type]>
		<#if tagType?starts_with("DecimalTag")>
			<@decimalTag field/>
		<#elseif tagType="LengthTag">
			<@typeTag field/>
		<#else>
			<@basicTag field/>
		</#if>
	<#else>
		<@unknownType field/>
	</#if>
</#list>
}
</#list>
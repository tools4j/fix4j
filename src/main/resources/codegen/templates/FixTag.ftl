<@pp.dropOutputFile />
<#list fields?keys as versionString>
<#assign version=versionString?substring(3)>
<#assign doc=fields[versionString]>
<@pp.changeOutputFile name=pp.home + "org/fix4j/engine/fix/" + versionString + "/FixTag" + version + ".java" />
package org.fixj.engine.fix.${versionString};

import org.fix4j.engine.tag.*;
import org.fix4j.engine.tag.impl.*;

public final class FixTag${version} {
<#list doc.Fields.Field as field>
	/** ${field.Description?xhtml?chop_linebreak} */
	<#if mappings[field.Type]??>
	<#assign tagType=mappings[field.Type]>
	public static final ${tagType} ${field.Name} = new Basic${tagType}("${field.Name}", ${field.Tag});
	<#else>
	public static final FixTag ${field.Name} = null;//No type-mapping for FIX type: ${field.Type}
	</#if>
</#list>
}
</#list>
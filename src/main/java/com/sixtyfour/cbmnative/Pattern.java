package com.sixtyfour.cbmnative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author EgonOlsen
 *
 */
public class Pattern
{
  private List<String> pattern;
  private int pos = 0;
  private String[] regs = new String[10];
  private String[] replacement;
  private int index = -1;
  private int end = -1;
  private String name;


  public Pattern(String name, String[] replacement, String... parts)
  {
    pattern = new ArrayList<>(Arrays.asList(parts));
    this.replacement = replacement;
    this.name = name;
  }


  public List<String> apply(List<String> code)
  {
    if (pos == pattern.size())
    {
      List<String> first = code.subList(0, index);
      List<String> last = code.subList(end + 1, code.size());
      List<String> eternity =
        replacement != null ? new ArrayList<String>(Arrays.asList(replacement)) : new ArrayList<String>();
      eternity.add("; Optimizer rule: " + name);
      List<String> res = new ArrayList<>(first);
      res.addAll(eternity);
      res.addAll(last);
      resetPattern();
      //Logger.log("Pattern '" + name + "' applied!");
      return res;
    }
    resetPattern();
    return code;
  }


  public boolean matches(String line, int ix)
  {
    String part = pattern.get(pos);
    line = line.trim();
    if (line.startsWith(";"))
    {
      return false;
    }
    int p0 = part.indexOf(" ");
    int p1 = line.indexOf(" ");
    if (p0 != -1 && p1 != -1)
    {
      //System.out.println("Checking: " + line + " / " + part);
      if (part.substring(0, p0).equalsIgnoreCase(line.substring(0, p1)))
      {
        String partRight = part.substring(p0 + 1).trim();
        String lineRight = line.substring(p1 + 1).trim();
        if (partRight.equalsIgnoreCase(lineRight))
        {
          return inc(ix);
        }
        else
        {
          p0 = partRight.indexOf("{");
          if (p0 != -1)
          {
            String leftPart = partRight.substring(0, p0);
            if (lineRight.startsWith(leftPart))
            {
              String value = lineRight.substring(p0);
              p1 = partRight.lastIndexOf("}");
              String reg = partRight.substring(p0 + 1, p1);
              if (lineRight.endsWith("_REG"))
              {
                int num = Integer.parseInt(reg.replace("REG", ""));
                if (regs[num] == null)
                {
                  regs[num] = value;
                  return inc(ix);
                }
                else
                {
                  if (regs[num].equalsIgnoreCase(value))
                  {
                    return inc(ix);
                  }
                  else
                  {
                    return resetPattern();
                  }
                }
              }
              else
              {
                return resetPattern();
              }
            }
            else
            {
              return resetPattern();
            }
          }
          else
          {
            return resetPattern();
          }
        }
      }
    }
    return resetPattern();
  }


  private boolean resetPattern()
  {
    pos = 0;
    index = -1;
    end = -1;
    regs = new String[regs.length];
    return false;
  }


  private boolean inc(int ix)
  {
    pos++;
    if (index == -1)
    {
      index = ix;
    }
    end = ix;
    return pos == pattern.size();
  }

}

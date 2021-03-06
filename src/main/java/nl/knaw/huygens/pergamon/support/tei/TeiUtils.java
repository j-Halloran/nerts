package nl.knaw.huygens.pergamon.support.tei;

import java.util.Collection;
import java.util.Optional;

import nl.knaw.huygens.tei.Element;

/**
 * Utility methods for handling TEI.
 */
public class TeiUtils {

  /**
   * Sets the @xml:id of the specified element to a normalized value.
   */
  public static void setNormalizedXmlIdAttribute(Element element, int number) {
    String value = String.format("%s-%d", element.getName(), number);
    element.setAttribute(Attributes.ATTR_XML_ID, value);
  }

  public static String getIdAttribute(Element element) {
    String value = element.getAttribute(Attributes.ATTR_XML_ID);
    return value.isEmpty() ? element.getAttribute(Attributes.ATTR_ID) : value;
  }

  public static void setIdAttribute(Element element, String value) {
    element.setAttribute(Attributes.ATTR_XML_ID, value);
    element.removeAttribute(Attributes.ATTR_ID);
  }

  public static void copyIdAttribute(Element source, Element target) {
    setIdAttribute(target, getIdAttribute(source));
  }

  public static void removeIdAttribute(Element element) {
    element.removeAttribute(Attributes.ATTR_XML_ID);
    element.removeAttribute(Attributes.ATTR_ID);
  }

  // ---------------------------------------------------------------------------

  public static String getResponsibility(Element element) {
    return element.getAttribute(Attributes.RESP);
  }

  public static void setResponsibility(Element element, String value) {
    element.setAttribute(Attributes.RESP, value.trim());
  }

  public static void setResponsibility(Element element, Optional<String> name) {
    if (name.isPresent()) {
      setResponsibility(element, nameRef(name.get()));
    }
  }

  /**
   * Set the responsibility to the specified value if the element has none.
   */
  public static void ensureResponsibility(Element element, String value) {
    if (getResponsibility(element).isEmpty()) {
      element.setAttribute(Attributes.RESP, nameRef(value));
    }
  }

  public static void appendResponsibility(Element element, Optional<String> annotator) {
    if (annotator.isPresent()) {
      setResponsibility(element, getResponsibility(element) + " " + nameRef(annotator.get()));
    }
  }

  private static String nameRef(String name) {
    return name.startsWith("#") ? name : "#" + name;
  }

  // ---------------------------------------------------------------------------

  /**
   * Returns the nearest ancestor with the specified name,
   * if such an element exists.
   */
  public static Optional<Element> getAncestor(Element element, String name) {
    element = element.getParent();
    while (element != null) {
      if (element.hasName(name)) {
        return Optional.of(element);
      }
      element = element.getParent();
    }
    return Optional.empty();
  }

  /**
   * Returns {@code true} if the specified element has an ancestor
   * with the specified name, {@code false} otherwise.
   */
  public static boolean hasAncestor(Element element, String name) {
    return getAncestor(element, name).isPresent();
  }

  /**
   * Returns {@code true} if the specified element has an ancestor
   * with one of the specified names, {@code false} otherwise.
   */
  public static boolean hasAncestor(Element element, Collection<String> names) {
    element = element.getParent();
    while (element != null) {
      if (names.contains(element.getName())) {
        return true;
      }
      element = element.getParent();
    }
    return false;
  }

  /**
   * Returns the value of the attribute with the specified key if present,
   * otherwise the corresponding value for the parent element, recursively.
   */
  public static String getImpliedAttribute(Element element, String key, String defaultValue) {
    while (element != null) {
      String language = element.getAttribute(key);
      if (!language.isEmpty() && !language.equals("?")) {
        return language;
      }
      element = element.getParent();
    }
    return defaultValue;
  }

  public static String getImpliedLanguage(Element element) {
    return getImpliedAttribute(element, Element.LANGUAGE, "?");
  }

  public static String openTag(String tagName, String key, String value) {
    StringBuilder builder = new StringBuilder();
    new Element(tagName).withAttribute(key, value).appendOpenTagTo(builder);
    return builder.toString();
  }

  public static String closeTag(String tagName) {
    StringBuilder builder = new StringBuilder();
    new Element(tagName).appendCloseTagTo(builder);
    return builder.toString();
  }

  private TeiUtils() {
    throw new AssertionError("Non-instantiable class");
  }

}
